package com.project.tesi.service;

import com.project.tesi.dto.request.SendMessageRequest;
import com.project.tesi.dto.response.ChatMessageResponse;
import com.project.tesi.dto.response.ConversationPreviewResponse;

import java.util.List;

public interface ChatService {

    ChatMessageResponse sendMessage(SendMessageRequest request);

    /**
     * Salva direttamente un messaggio passando senderId, receiverId e content
     * (usato dalla WebSocket).
     * Gestisce la transazione correttamente tramite il proxy Spring.
     */
    void sendMessageDirect(Long senderId, Long receiverId, String content);

    List<ChatMessageResponse> getConversation(Long userId1, Long userId2, int page, int size);

    List<ConversationPreviewResponse> getUserConversations(Long userId);

    void markAsRead(Long receiverId, Long senderId);

    int getTotalUnreadCount(Long userId);
}
