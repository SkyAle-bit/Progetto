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

    /** ID dell'altro utente nella conversazione. */
    private Long otherUserId;

    /** Nome completo dell'altro utente. */
    private String otherUserName;

    /** Ruolo dell'altro utente (es. "PERSONAL_TRAINER", "CLIENT"). */
    private String otherUserRole;

    /** Testo dell'ultimo messaggio scambiato (per l'anteprima). */
    private String lastMessage;

    /** Data e ora dell'ultimo messaggio (per l'ordinamento delle conversazioni). */
    private LocalDateTime lastMessageTime;

    /** Numero di messaggi non letti in questa conversazione. */
    private int unreadCount;

    /** Indica se l'utente ha terminato questa conversazione (visibile solo all'operatore). */
    @Builder.Default
    private boolean terminated = false;
}
