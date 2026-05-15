package com.project.tesi.dto.request.ws;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record WsSendMessageRequest(
        @NotNull Long chatId,
        @NotBlank @Size(max = 2000) String content
) {}
