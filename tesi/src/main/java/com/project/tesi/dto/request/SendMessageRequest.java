package com.project.tesi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di richiesta per l'invio di un messaggio in chat.
 * Usato dall'endpoint REST {@code POST /api/chat/send}.
 * Per l'invio via WebSocket si usa un payload Map diretto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dati per l'invio di un messaggio in chat")
public class SendMessageRequest {

    /** ID dell'utente che invia il messaggio. */
    @Schema(description = "ID dell'utente che invia il messaggio", example = "1")
    @NotNull(message = "L'ID del mittente è obbligatorio")
    private Long senderId;

    /** ID dell'utente destinatario del messaggio. */
    @Schema(description = "ID dell'utente destinatario", example = "5")
    @NotNull(message = "L'ID del destinatario è obbligatorio")
    private Long receiverId;

    /** Contenuto testuale del messaggio (massimo 2000 caratteri). */
    @Schema(description = "Contenuto del messaggio", example = "Ciao, come stai?")
    @NotBlank(message = "Il messaggio non può essere vuoto")
    @Size(max = 2000, message = "Il messaggio non può superare i 2000 caratteri")
    private String content;
}
