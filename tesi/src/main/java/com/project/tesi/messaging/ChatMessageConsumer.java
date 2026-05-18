package com.project.tesi.messaging;

import com.project.tesi.config.RabbitMQConfig;
import com.project.tesi.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMessageConsumer {

    private final ChatService chatService;

    public ChatMessageConsumer(ChatService chatService) {
        this.chatService = chatService;
    }

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void consume(ChatMessagePayload payload) {
        log.info("[RabbitMQ] Consume chat message chatId={} senderId={}", payload.chatId(), payload.senderId());
        try {
            chatService.sendMessageDirect(payload.chatId(), payload.senderId(), payload.content());
        } catch (Exception e) {
            log.error("[RabbitMQ] Errore durante save asincrono chatId={} senderId={}: {}",
                    payload.chatId(), payload.senderId(), e.getMessage(), e);
            throw e;
        }
    }
}
