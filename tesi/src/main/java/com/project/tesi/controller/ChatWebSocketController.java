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
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final WebSocketEventListener eventListener;
    private final ChatService chatService;

    @MessageMapping("/chat.join")
    public void joinRoom(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor ha) {
        String sid = ha.getSessionId();
        String rid = (String) payload.get("roomId"); // roomId is now chatId
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
        Long chatId = toLong(payload.get("chatId"));
        String content = (String) payload.get("content");

        if (senderId == null || chatId == null || content == null)
            return;

        String roomId = String.valueOf(chatId);

        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", System.currentTimeMillis());
        dto.put("senderId", senderId);
        dto.put("senderName", chatService.getUserFullName(senderId));
        dto.put("chatId", chatId);
        dto.put("content", content);
        dto.put("status", "SENT");
        dto.put("createdAt", now.toString());
        dto.put("roomId", roomId);

        Long receiverId = null;
        try {
            com.project.tesi.model.Chat chat = chatService.getChatEntity(chatId);
            if (chat != null) {
                receiverId = chat.getUser1().getId().equals(senderId) ? chat.getUser2().getId() : chat.getUser1().getId();
                dto.put("receiverId", receiverId);
                dto.put("receiverName", chatService.getUserFullName(receiverId));
            }
        } catch (Exception e) { /* ignore */ }

        messagingTemplate.convertAndSend("/topic/chat/" + roomId, (Object) dto);

        saveMessageAsync(chatId, senderId, content);

        if (receiverId != null) {
            try {
                Map<String, Object> notification = new HashMap<>();
                notification.put("type", "NEW_MESSAGE");
                notification.put("message", dto);
                messagingTemplate.convertAndSend("/user/" + receiverId + "/queue/notifications", (Object) notification);
            } catch (Exception e) { /* ignore */ }
            sendUnreadUpdate(receiverId);
        }
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Map<String, Object> payload) {
        Long uid = toLong(payload.get("userId"));
        Long chatId = toLong(payload.get("chatId"));
        if (uid != null && chatId != null) {
            markAsReadAsync(chatId, uid);
            sendUnreadUpdate(uid);
        }
    }

    @Async
    public void saveMessageAsync(Long chatId, Long senderId, String content) {
        try {
            chatService.sendMessageDirect(chatId, senderId, content);
        } catch (Exception e) {
            System.err.println("[WS] Save error: " + e.getMessage());
        }
    }

    @Async
    public void markAsReadAsync(Long chatId, Long userId) {
        try {
            chatService.markAsRead(chatId, userId);
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
