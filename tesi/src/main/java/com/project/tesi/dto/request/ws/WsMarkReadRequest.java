package com.project.tesi.dto.request.ws;

import jakarta.validation.constraints.NotNull;

public record WsMarkReadRequest(@NotNull Long chatId) {}
