package com.project.tesi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di richiesta per l'invio di un messaggio in chat.
 * Usato dall'endpoint REST {@code POST /api/chat/send}.
 * Per l'invio via WebSocket si usa un payload Map diretto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @NotNull(message = "L'ID del mittente è obbligatorio")
    private Long senderId;

    @NotNull(message = "L'ID della chat è obbligatorio")
    private Long chatId;

    @NotBlank(message = "Il contenuto del messaggio non può essere vuoto")
    private String content;

}
