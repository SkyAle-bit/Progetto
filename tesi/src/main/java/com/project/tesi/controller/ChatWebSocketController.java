package com.project.tesi.controller;

import com.project.tesi.config.WebSocketEventListener;
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

/**
 * Controller WebSocket (STOMP) per la chat in tempo reale.
 *
 * Gestisce tre operazioni tramite messaggi STOMP:
 * <ul>
 *   <li>{@code /app/chat.join} — l'utente entra in una stanza chat</li>
 *   <li>{@code /app/chat.leave} — l'utente esce da una stanza chat</li>
 *   <li>{@code /app/chat.send} — l'utente invia un messaggio</li>
 *   <li>{@code /app/chat.read} — l'utente segna i messaggi come letti</li>
 * </ul>
 *
 * L'invio dei messaggi segue un pattern "fire-and-persist":
 * il messaggio viene inoltrato immediatamente via WebSocket alla stanza,
 * e poi salvato in modo asincrono nel database tramite {@link ChatService}.
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final WebSocketEventListener eventListener;
    private final ChatService chatService;

    /** Registra l'ingresso dell'utente in una stanza chat (per tracciare la presenza). */
    @MessageMapping("/chat.join")
    public void joinRoom(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor ha) {
        String sid = ha.getSessionId();
        String rid = (String) payload.get("roomId");
        if (sid != null && rid != null)
            eventListener.joinRoom(sid, rid);
    }

    /** Registra l'uscita dell'utente da una stanza chat. */
    @MessageMapping("/chat.leave")
    public void leaveRoom(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor ha) {
        String sid = ha.getSessionId();
        String rid = (String) payload.get("roomId");
        if (sid != null && rid != null)
            eventListener.leaveRoom(sid, rid);
    }

    /**
     * Gestisce l'invio di un messaggio in tempo reale.
     * 1) Inoltra immediatamente il messaggio alla stanza via WebSocket
     * 2) Persiste il messaggio nel database in modo asincrono
     * 3) Se il destinatario non è nella stanza, invia una notifica push
     */
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
        dto.put("senderName", chatService.getUserFullName(senderId));
        dto.put("receiverId", receiverId);
        dto.put("receiverName", chatService.getUserFullName(receiverId));
        dto.put("content", content);
        dto.put("status", "SENT");
        dto.put("createdAt", now.toString());
        dto.put("roomId", roomId);

        messagingTemplate.convertAndSend("/topic/room/" + roomId, (Object) dto);

        saveMessageAsync(senderId, receiverId, content);

        if (!eventListener.isUserInRoom(receiverId, roomId)) {
            Map<String, Object> notif = new HashMap<>();
            notif.put("type", "NEW_MESSAGE");
            notif.put("message", dto);
            messagingTemplate.convertAndSend("/user/" + receiverId + "/queue/notifications", (Object) notif);
            sendUnreadUpdate(receiverId);
        }
    }

    /** Segna come letti tutti i messaggi di una conversazione e aggiorna il badge non letti. */
    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Map<String, Object> payload) {
        Long uid = toLong(payload.get("userId"));
        Long oid = toLong(payload.get("otherUserId"));
        if (uid != null && oid != null) {
            markAsReadAsync(uid, oid);
            sendUnreadUpdate(uid);
        }
    }

    @Async
    public void saveMessageAsync(Long senderId, Long receiverId, String content) {
        try {
            chatService.sendMessageDirect(senderId, receiverId, content);
        } catch (Exception e) {
            System.err.println("[WS] Save error: " + e.getMessage());
        }
    }

    @Async
    public void markAsReadAsync(Long receiverId, Long senderId) {
        try {
            chatService.markAsRead(receiverId, senderId);
        } catch (Exception e) {
            System.err.println("[WS] MarkAsRead error: " + e.getMessage());
        }
    }

    private void sendUnreadUpdate(Long userId) {
        try {
            int count = chatService.getTotalUnreadCount(userId);
            Map<String, Object> update = new HashMap<>();
            update.put("type", "UNREAD_UPDATE");
            update.put("userId", userId);
            update.put("unreadCount", count);
            messagingTemplate.convertAndSend("/user/" + userId + "/queue/notifications", (Object) update);
        } catch (Exception e) {
            /* ignore */ }
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
