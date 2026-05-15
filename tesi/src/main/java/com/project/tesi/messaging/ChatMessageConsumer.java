package com.project.tesi.messaging;

import com.project.tesi.config.RabbitMQConfig;
import com.project.tesi.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageConsumer {

    private final ChatService chatService;

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE)
    public void consume(ChatMessagePayload payload) {
        chatService.sendMessageDirect(payload.chatId(), payload.senderId(), payload.content());
    }
}
