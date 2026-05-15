package com.project.tesi.messaging;

public record ChatMessagePayload(Long chatId, Long senderId, String content) {}
