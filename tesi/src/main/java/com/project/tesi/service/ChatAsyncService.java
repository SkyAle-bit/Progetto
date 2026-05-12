package com.project.tesi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servizio dedicato alle operazioni asincrone della chat.
 * L'estrazione in un @Service separato garantisce che le chiamate passino
 * attraverso il proxy di Spring, facendo funzionare correttamente @Async.
 */
@Service
@RequiredArgsConstructor
public class ChatAsyncService {

    private final ChatService chatService;

    @Async
    public void saveMessageAsync(Long chatId, Long senderId, String content) {
        try {
            chatService.sendMessageDirect(chatId, senderId, content);
        } catch (Exception e) {
            System.err.println("[WS] Save error: " + e.getMessage());
        }
    }

    @Async
    public void markAsReadAsync(Long chatId, Long userId) {
        try {
            chatService.markAsRead(chatId, userId);
        } catch (Exception e) {
            System.err.println("[WS] MarkAsRead error: " + e.getMessage());
        }
    }
}
