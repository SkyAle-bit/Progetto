package com.project.tesi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
        @NotNull(message = "L'ID della chat è obbligatorio") Long chatId,
        @NotBlank(message = "Il contenuto del messaggio non può essere vuoto") String content) {
}
