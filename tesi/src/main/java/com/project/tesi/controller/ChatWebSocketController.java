package com.project.tesi.controller;

import com.project.tesi.config.WebSocketEventListener;
import com.project.tesi.repository.ChatMessageRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final WebSocketEventListener eventListener;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;

    @MessageMapping("/chat.join")
    public void joinRoom(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor ha) {
        String sid = ha.getSessionId();
        String rid = (String) payload.get("roomId");
        if (sid != null && rid != null)
            eventListener.joinRoom(sid, rid);
    }

    @MessageMapping("/chat.leave")
    public void leaveRoom(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor ha) {
        String sid = ha.getSessionId();
        String rid = (String) payload.get("roomId");
        if (sid != null && rid != null)
            eventListener.leaveRoom(sid, rid);
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Map<String, Object> payload) {
        Long senderId = toLong(payload.get("senderId"));
        Long receiverId = toLong(payload.get("receiverId"));
        String content = (String) payload.get("content");
        String roomId = (String) payload.get("roomId");
        if (senderId == null || receiverId == null || content == null || roomId == null)
            return;

        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", System.currentTimeMillis());
        dto.put("senderId", senderId);
        dto.put("senderName", getUserName(senderId));
        dto.put("receiverId", receiverId);
        dto.put("receiverName", getUserName(receiverId));
        dto.put("content", content);
        dto.put("status", "SENT");
        dto.put("createdAt", now.toString());
        dto.put("roomId", roomId);

        // STEP 1: Inoltra IMMEDIATAMENTE alla stanza
        messagingTemplate.convertAndSend("/topic/room/" + roomId, (Object) dto);

        // STEP 2: Salva in DB in modo ASINCRONO – delegate al Service per
        // @Transactional corretto
        saveMessageAsync(senderId, receiverId, content);

        // STEP 3: Se receiver non in stanza, notifica push
        if (!eventListener.isUserInRoom(receiverId, roomId)) {
            Map<String, Object> notif = new HashMap<>();
            notif.put("type", "NEW_MESSAGE");
            notif.put("message", dto);
            messagingTemplate.convertAndSend("/user/" + receiverId + "/queue/notifications", (Object) notif);
            sendUnreadUpdate(receiverId);
        }
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Map<String, Object> payload) {
        Long uid = toLong(payload.get("userId"));
        Long oid = toLong(payload.get("otherUserId"));
        if (uid != null && oid != null) {
            // Delegato al Service che ha l'AOP proxy corretto per @Transactional
            markAsReadAsync(uid, oid);
            // Aggiorna il contatore non letti in tempo reale per il client
            sendUnreadUpdate(uid);
        }
    }

    @Async
    public void saveMessageAsync(Long senderId, Long receiverId, String content) {
        try {
            // Delegato al Service correttamente proxied per gestire la transazione
            chatService.sendMessageDirect(senderId, receiverId, content);
        } catch (Exception e) {
            System.err.println("[WS] Save error: " + e.getMessage());
        }
    }

    @Async
    public void markAsReadAsync(Long receiverId, Long senderId) {
        try {
            // Delegato al Service correttamente proxied per gestire la transazione
            chatService.markAsRead(receiverId, senderId);
        } catch (Exception e) {
            System.err.println("[WS] MarkAsRead error: " + e.getMessage());
        }
    }

    private void sendUnreadUpdate(Long userId) {
        try {
            int count = chatMessageRepository.countAllUnreadMessages(userId);
            Map<String, Object> update = new HashMap<>();
            update.put("type", "UNREAD_UPDATE");
            update.put("userId", userId);
            update.put("unreadCount", count);
            messagingTemplate.convertAndSend("/user/" + userId + "/queue/notifications", (Object) update);
        } catch (Exception e) {
            /* ignore */ }
    }

    private String getUserName(Long userId) {
        try {
            Optional<com.project.tesi.model.User> user = userRepository.findById(userId);
            return user.map(u -> u.getFirstName() + " " + u.getLastName()).orElse("Utente");
        } catch (Exception e) {
            return "Utente";
        }
    }

    private Long toLong(Object value) {
        if (value == null)
            return null;
        if (value instanceof Number)
            return ((Number) value).longValue();
        if (value instanceof String)
            return Long.parseLong((String) value);
        return null;
    }
}
