package com.project.tesi.controller;

import com.project.tesi.config.WebSocketEventListener;
import com.project.tesi.dto.request.ws.JoinRoomRequest;
import com.project.tesi.dto.request.ws.LeaveRoomRequest;
import com.project.tesi.dto.request.ws.WsMarkReadRequest;
import com.project.tesi.dto.request.ws.WsSendMessageRequest;
import com.project.tesi.dto.response.WsMessageResponse;
import com.project.tesi.dto.response.WsNotificationResponse;
import com.project.tesi.dto.response.WsUnreadUpdateResponse;
import com.project.tesi.messaging.ChatMessagePublisher;
import com.project.tesi.model.Chat;
import com.project.tesi.model.User;
import com.project.tesi.service.ChatAsyncService;
import com.project.tesi.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

/**
 * Controller WebSocket (STOMP) per la chat in tempo reale.
 * L'identità del mittente è estratta dal JWT tramite il Principal iniettato da
 * WebSocketChannelInterceptor — mai dal payload del messaggio.
 */
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final WebSocketEventListener eventListener;
    private final ChatService chatService;
    private final ChatAsyncService chatAsyncService;
    private final ChatMessagePublisher chatMessagePublisher;

    @MessageMapping("/chat.join")
    public void joinRoom(@Payload JoinRoomRequest request, SimpMessageHeaderAccessor ha) {
        String sid = ha.getSessionId();
        if (sid != null && request.roomId() != null) {
            eventListener.joinRoom(sid, request.roomId());
        }
    }

    @MessageMapping("/chat.leave")
    public void leaveRoom(@Payload LeaveRoomRequest request, SimpMessageHeaderAccessor ha) {
        String sid = ha.getSessionId();
        if (sid != null && request.roomId() != null) {
            eventListener.leaveRoom(sid, request.roomId());
        }
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload WsSendMessageRequest request, Principal principal) {
        User sender = extractUser(principal);
        if (sender == null) return;

        Long senderId = sender.getId();
        Long chatId = request.chatId();
        String content = request.content();
        String roomId = String.valueOf(chatId);

        WsMessageResponse msg = WsMessageResponse.builder()
                .id(System.currentTimeMillis())
                .senderId(senderId)
                .senderName(chatService.getUserFullName(senderId))
                .chatId(chatId)
                .content(content)
                .status("SENT")
                .createdAt(LocalDateTime.now().toString())
                .roomId(roomId)
                .build();

        Long receiverId = null;
        try {
            Chat chat = chatService.getChatEntity(chatId);
            if (chat != null) {
                receiverId = chat.getUser1().getId().equals(senderId)
                        ? chat.getUser2().getId()
                        : chat.getUser1().getId();
                msg = WsMessageResponse.builder()
                        .id(msg.getId())
                        .senderId(msg.getSenderId())
                        .senderName(msg.getSenderName())
                        .chatId(msg.getChatId())
                        .content(msg.getContent())
                        .status(msg.getStatus())
                        .createdAt(msg.getCreatedAt())
                        .roomId(msg.getRoomId())
                        .receiverId(receiverId)
                        .receiverName(chatService.getUserFullName(receiverId))
                        .build();
            }
        } catch (Exception e) { /* chat non trovata — messaggio senza receiverId */ }

        messagingTemplate.convertAndSend("/topic/chat/" + roomId, msg);

        chatMessagePublisher.publish(chatId, senderId, content);

        if (receiverId != null) {
            try {
                messagingTemplate.convertAndSend(
                        "/user/" + receiverId + "/queue/notifications",
                        new WsNotificationResponse("NEW_MESSAGE", msg));
            } catch (Exception e) { /* ignore */ }
            sendUnreadUpdate(receiverId);
        }
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload WsMarkReadRequest request, Principal principal) {
        User user = extractUser(principal);
        if (user == null) return;
        chatAsyncService.markAsReadAsync(request.chatId(), user.getId());
        sendUnreadUpdate(user.getId());
    }

    private void sendUnreadUpdate(Long userId) {
        try {
            int count = chatService.getTotalUnreadCount(userId);
            messagingTemplate.convertAndSend(
                    "/user/" + userId + "/queue/notifications",
                    new WsUnreadUpdateResponse("UNREAD_UPDATE", userId, count));
        } catch (Exception e) { /* ignore */ }
    }

    private User extractUser(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth
                && auth.getPrincipal() instanceof User user) {
            return user;
        }
        return null;
    }
}
