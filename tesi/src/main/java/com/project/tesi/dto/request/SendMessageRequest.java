package com.project.tesi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dati per l'invio di un messaggio in chat")
public class SendMessageRequest {

    @Schema(description = "ID dell'utente che invia il messaggio", example = "1")
    @NotNull(message = "L'ID del mittente è obbligatorio")
    private Long senderId;

    @Schema(description = "ID dell'utente destinatario", example = "5")
    @NotNull(message = "L'ID del destinatario è obbligatorio")
    private Long receiverId;

    @Schema(description = "Contenuto del messaggio", example = "Ciao, come stai?")
    @NotBlank(message = "Il messaggio non può essere vuoto")
    @Size(max = 2000, message = "Il messaggio non può superare i 2000 caratteri")
    private String content;
}

