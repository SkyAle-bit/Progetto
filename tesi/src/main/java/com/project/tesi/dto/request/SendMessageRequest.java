package com.project.tesi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO di richiesta per l'invio di un messaggio in chat.
 * Usato dall'endpoint REST {@code POST /api/chat/send}.
 * Per l'invio via WebSocket si usa un payload Map diretto.
 */
@Data
public class SendMessageRequest {

    @NotNull(message = "L'ID del mittente è obbligatorio")
    private Long senderId;

    @NotNull(message = "L'ID della chat è obbligatorio")
    private Long chatId;

    @NotBlank(message = "Il contenuto del messaggio non può essere vuoto")
    private String content;


    private SendMessageRequest() {}

    public static class Builder {
        private Long senderId;
        private Long chatId;
        private String content;

        public Builder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public SendMessageRequest build() {
            SendMessageRequest obj = new SendMessageRequest();
            obj.senderId = this.senderId;
            obj.chatId = this.chatId;
            obj.content = this.content;
            return obj;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
