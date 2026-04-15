package com.project.tesi.controller;

import com.project.tesi.config.WebSocketEventListener;
import com.project.tesi.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test unitari per {@link ChatWebSocketController}.
 */
@ExtendWith(MockitoExtension.class)
class ChatWebSocketControllerTest {

    @Mock private SimpMessageSendingOperations messagingTemplate;
    @Mock private WebSocketEventListener eventListener;
    @Mock private ChatService chatService;

    @InjectMocks private ChatWebSocketController controller;


    @Test @DisplayName("joinRoom — sessione e roomId validi → registra ingresso")
    void joinRoom_success() {
        Map<String, Object> payload = Map.of("roomId", "room-1-2");
        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor.create();
        ha.setSessionId("session-123");

        controller.joinRoom(payload, ha);

        verify(eventListener).joinRoom("session-123", "room-1-2");
    }

    @Test @DisplayName("joinRoom — sessionId null → non registra")
    void joinRoom_nullSession() {
        Map<String, Object> payload = Map.of("roomId", "room-1-2");
        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor.create();
        // sessionId è null di default

        controller.joinRoom(payload, ha);

        verify(eventListener, never()).joinRoom(any(), any());
    }

    @Test @DisplayName("joinRoom — roomId null → non registra")
    void joinRoom_nullRoomId() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("roomId", null);
        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor.create();
        ha.setSessionId("session-123");

        controller.joinRoom(payload, ha);

        verify(eventListener, never()).joinRoom(any(), any());
    }


    @Test @DisplayName("leaveRoom — sessione e roomId validi → registra uscita")
    void leaveRoom_success() {
        Map<String, Object> payload = Map.of("roomId", "room-1-2");
        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor.create();
        ha.setSessionId("session-123");

        controller.leaveRoom(payload, ha);

        verify(eventListener).leaveRoom("session-123", "room-1-2");
    }

    @Test @DisplayName("leaveRoom — sessionId null → non registra")
    void leaveRoom_nullSession() {
        Map<String, Object> payload = Map.of("roomId", "room-1-2");
        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor.create();

        controller.leaveRoom(payload, ha);

        verify(eventListener, never()).leaveRoom(any(), any());
    }

    @Test @DisplayName("leaveRoom — roomId null → non registra")
    void leaveRoom_nullRoomId() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("roomId", null);
        SimpMessageHeaderAccessor ha = SimpMessageHeaderAccessor.create();
        ha.setSessionId("session-123");

        controller.leaveRoom(payload, ha);

        verify(eventListener, never()).leaveRoom(any(), any());
    }


    @Test @DisplayName("sendMessage — tutti i campi validi → inoltra e salva")
    void sendMessage_success() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", 1L);
        payload.put("receiverId", 2L);
        payload.put("content", "Ciao!");
        payload.put("roomId", "room-1-2");

        when(chatService.getUserFullName(1L)).thenReturn("Mario Rossi");
        when(chatService.getUserFullName(2L)).thenReturn("Luca Bianchi");
        when(eventListener.isUserInRoom(2L, "room-1-2")).thenReturn(true);

        controller.sendMessage(payload);

        verify(messagingTemplate).convertAndSend(eq("/topic/room/room-1-2"), any(Object.class));
    }

    @Test @DisplayName("sendMessage — receiver non in stanza → invia notifica push")
    void sendMessage_receiverNotInRoom() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", 1L);
        payload.put("receiverId", 2L);
        payload.put("content", "Ciao!");
        payload.put("roomId", "room-1-2");

        when(chatService.getUserFullName(1L)).thenReturn("Mario Rossi");
        when(chatService.getUserFullName(2L)).thenReturn("Luca Bianchi");
        when(eventListener.isUserInRoom(2L, "room-1-2")).thenReturn(false);
        when(chatService.getTotalUnreadCount(2L)).thenReturn(3);

        controller.sendMessage(payload);

        // Verifica che il messaggio sia stato inoltrato alla stanza
        verify(messagingTemplate).convertAndSend(eq("/topic/room/room-1-2"), any(Object.class));
        // Verifica notifica push al receiver (1 notifica NEW_MESSAGE + 1 UNREAD_UPDATE = 2 invii)
        verify(messagingTemplate, times(2)).convertAndSend(eq("/user/2/queue/notifications"), any(Object.class));
    }

    @Test @DisplayName("sendMessage — senderId null → esce senza fare nulla")
    void sendMessage_nullSenderId() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", null);
        payload.put("receiverId", 2L);
        payload.put("content", "Ciao!");
        payload.put("roomId", "room-1-2");

        controller.sendMessage(payload);

        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test @DisplayName("sendMessage — content null → esce senza fare nulla")
    void sendMessage_nullContent() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", 1L);
        payload.put("receiverId", 2L);
        payload.put("content", null);
        payload.put("roomId", "room-1-2");

        controller.sendMessage(payload);

        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test @DisplayName("sendMessage — roomId null → esce senza fare nulla")
    void sendMessage_nullRoomId() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", 1L);
        payload.put("receiverId", 2L);
        payload.put("content", "Ciao!");
        payload.put("roomId", null);

        controller.sendMessage(payload);

        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test @DisplayName("sendMessage — senderId come stringa → converte in Long")
    void sendMessage_senderIdAsString() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", "1");
        payload.put("receiverId", "2");
        payload.put("content", "Ciao!");
        payload.put("roomId", "room-1-2");

        when(chatService.getUserFullName(1L)).thenReturn("Mario");
        when(chatService.getUserFullName(2L)).thenReturn("Luca");
        when(eventListener.isUserInRoom(2L, "room-1-2")).thenReturn(true);

        controller.sendMessage(payload);

        verify(messagingTemplate).convertAndSend(eq("/topic/room/room-1-2"), any(Object.class));
    }


    @Test @DisplayName("markAsRead — userId e otherUserId validi → segna e aggiorna")
    void markAsRead_success() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 1L);
        payload.put("otherUserId", 2L);

        when(chatService.getTotalUnreadCount(1L)).thenReturn(0);

        controller.markAsRead(payload);

        verify(messagingTemplate).convertAndSend(eq("/user/1/queue/notifications"), any(Object.class));
    }

    @Test @DisplayName("markAsRead — userId null → non fa nulla")
    void markAsRead_nullUserId() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", null);
        payload.put("otherUserId", 2L);

        controller.markAsRead(payload);

        verify(chatService, never()).markAsRead(anyLong(), anyLong());
    }

    @Test @DisplayName("markAsRead — otherUserId null → non fa nulla")
    void markAsRead_nullOtherUserId() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 1L);
        payload.put("otherUserId", null);

        controller.markAsRead(payload);

        verify(chatService, never()).markAsRead(anyLong(), anyLong());
    }


    @Test @DisplayName("saveMessageAsync — successo")
    void saveMessageAsync_success() {
        controller.saveMessageAsync(1L, 2L, "Test");
        verify(chatService).sendMessageDirect(1L, 2L, "Test");
    }

    @Test @DisplayName("saveMessageAsync — eccezione non propagata")
    void saveMessageAsync_exception() {
        doThrow(new RuntimeException("DB error")).when(chatService).sendMessageDirect(1L, 2L, "Test");
        controller.saveMessageAsync(1L, 2L, "Test"); // non lancia eccezione
    }


    @Test @DisplayName("markAsReadAsync — successo")
    void markAsReadAsync_success() {
        controller.markAsReadAsync(1L, 2L);
        verify(chatService).markAsRead(1L, 2L);
    }

    @Test @DisplayName("markAsReadAsync — eccezione non propagata")
    void markAsReadAsync_exception() {
        doThrow(new RuntimeException("DB error")).when(chatService).markAsRead(1L, 2L);
        controller.markAsReadAsync(1L, 2L); // non lancia eccezione
    }


    @Test @DisplayName("sendUnreadUpdate — eccezione getTotalUnreadCount non propagata")
    void sendUnreadUpdate_exception() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 1L);
        payload.put("otherUserId", 2L);

        when(chatService.getTotalUnreadCount(1L)).thenThrow(new RuntimeException("DB error"));

        // Non deve lanciare eccezione
        controller.markAsRead(payload);
    }


    @Test @DisplayName("sendMessage — senderId tipo non supportato (Boolean) → null → esce")
    void sendMessage_unsupportedType() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("senderId", true); // tipo non supportato per toLong
        payload.put("receiverId", 2L);
        payload.put("content", "Ciao!");
        payload.put("roomId", "room-1-2");

        controller.sendMessage(payload);

        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }
}


