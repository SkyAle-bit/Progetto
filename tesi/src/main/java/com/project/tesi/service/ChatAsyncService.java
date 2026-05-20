package com.project.tesi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatAsyncService {

    private final ChatService chatService;

    public ChatAsyncService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Async("emailTaskExecutor")
    public void saveMessageAsync(Long chatId, Long senderId, String content) {
        try {
            chatService.sendMessageDirect(chatId, senderId, content);
        } catch (Exception e) {
            log.error("[WS] Save error chatId={} senderId={}: {}", chatId, senderId, e.getMessage(), e);
        }
    }

    @Async("emailTaskExecutor")
    public void markAsReadAsync(Long chatId, Long userId) {
        try {
            chatService.markAsRead(chatId, userId);
        } catch (Exception e) {
            log.error("[WS] MarkAsRead error chatId={} userId={}: {}", chatId, userId, e.getMessage(), e);
        }
    }
}
