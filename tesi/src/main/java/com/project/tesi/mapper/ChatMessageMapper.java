package com.project.tesi.mapper;

import com.project.tesi.dto.response.ChatMessageResponse;
import com.project.tesi.dto.response.ConversationPreviewResponse;
import com.project.tesi.model.ChatMessage;
import com.project.tesi.model.User;
import org.springframework.stereotype.Component;

/**
 * Mapper per la conversione delle entità chat nei DTO di risposta.
 * Gestisce sia i singoli messaggi ({@link ChatMessageResponse})
 * che le anteprime delle conversazioni ({@link ConversationPreviewResponse}).
 */
@Component
public class ChatMessageMapper {

    /**
     * Converte un'entità ChatMessage nel DTO di risposta.
     * Costruisce i nomi completi di mittente e destinatario.
     *
     * @param message l'entità messaggio (può essere null)
     * @return il DTO di risposta, oppure {@code null} se message è null
     */
    public ChatMessageResponse toResponse(ChatMessage message) {
        if (message == null) return null;

        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getFirstName() + " " + message.getReceiver().getLastName())
                .content(message.getContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * Costruisce l'anteprima di una conversazione per la lista chat.
     * Include l'ultimo messaggio scambiato e il conteggio dei non letti.
     *
     * @param otherUser   l'interlocutore nella conversazione
     * @param lastMessage ultimo messaggio scambiato (può essere null)
     * @param unreadCount numero di messaggi non letti
     * @return il DTO di anteprima, oppure {@code null} se otherUser è null
     */
    public ConversationPreviewResponse toConversationPreview(User otherUser, ChatMessage lastMessage, int unreadCount) {
        if (otherUser == null) return null;

        return ConversationPreviewResponse.builder()
                .otherUserId(otherUser.getId())
                .otherUserName(otherUser.getFirstName() + " " + otherUser.getLastName())
                .otherUserRole(otherUser.getRole().name())
                .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null)
                .unreadCount(unreadCount)
                .build();
    }
}
