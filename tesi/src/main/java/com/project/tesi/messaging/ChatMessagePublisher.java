package com.project.tesi.messaging;

import com.project.tesi.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(Long chatId, Long senderId, String content) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CHAT_EXCHANGE,
                RabbitMQConfig.CHAT_ROUTING_KEY,
                new ChatMessagePayload(chatId, senderId, content));
    }
}
