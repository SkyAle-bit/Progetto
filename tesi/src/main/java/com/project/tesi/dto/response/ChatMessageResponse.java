package com.project.tesi.dto.response;

import com.project.tesi.enums.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO di risposta per un messaggio della chat.
 * Usato sia nell'endpoint REST che nel payload WebSocket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    /** ID del messaggio. */
    private Long id;

    /** ID del mittente. */
    private Long senderId;

    /** Nome completo del mittente. */
    private String senderName;

    /** ID del destinatario. */
    private Long receiverId;

    /** Nome completo del destinatario. */
    private String receiverName;

    /** Contenuto testuale del messaggio. */
    private String content;

    /** Stato del messaggio (SENT, DELIVERED, READ). */
    private MessageStatus status;

    /** Data e ora di invio del messaggio. */
    private LocalDateTime createdAt;
}
