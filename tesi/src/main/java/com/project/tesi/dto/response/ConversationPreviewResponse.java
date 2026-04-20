package com.project.tesi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO di risposta per l'anteprima di una conversazione nella lista chat.
 * Mostra l'interlocutore, l'ultimo messaggio e il conteggio dei non letti.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationPreviewResponse {

    private Long chatId;

    private Long otherUserId;

    private String otherUserName;

    private String otherUserRole;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private int unreadCount;
}
