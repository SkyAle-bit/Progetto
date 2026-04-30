package com.project.tesi.dto.response;

import com.project.tesi.enums.MessageStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO di risposta per un messaggio della chat.
 * Usato sia nell'endpoint REST che nel payload WebSocket.
 */
@Data
public class ChatMessageResponse {

    private Long id;

    private Long chatId;

    private Long senderId;

    private String senderName;

    private Long receiverId;

    private String receiverName;

    private String content;

    private MessageStatus status;

    private LocalDateTime createdAt;

    private ChatMessageResponse() {}

    public static class Builder {
        private Long id;
        private Long chatId;
        private Long senderId;
        private String senderName;
        private Long receiverId;
        private String receiverName;
        private String content;
        private MessageStatus status;
        private LocalDateTime createdAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder senderId(Long senderId) {
            this.senderId = senderId;
            return this;
        }

        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }

        public Builder receiverId(Long receiverId) {
            this.receiverId = receiverId;
            return this;
        }

        public Builder receiverName(String receiverName) {
            this.receiverName = receiverName;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder status(MessageStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ChatMessageResponse build() {
            ChatMessageResponse obj = new ChatMessageResponse();
            obj.id = this.id;
            obj.chatId = this.chatId;
            obj.senderId = this.senderId;
            obj.senderName = this.senderName;
            obj.receiverId = this.receiverId;
            obj.receiverName = this.receiverName;
            obj.content = this.content;
            obj.status = this.status;
            obj.createdAt = this.createdAt;
            return obj;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
