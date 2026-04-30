package com.project.tesi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO di risposta per l'anteprima di una conversazione nella lista chat.
 * Mostra l'interlocutore, l'ultimo messaggio e il conteggio dei non letti.
 */
@Data
public class ConversationPreviewResponse {

    private Long chatId;

    private Long otherUserId;

    private String otherUserName;

    private String otherUserRole;

    private String lastMessage;

    private LocalDateTime lastMessageTime;

    private int unreadCount;

    private ConversationPreviewResponse() {}

    public static class Builder {
        private Long chatId;
        private Long otherUserId;
        private String otherUserName;
        private String otherUserRole;
        private String lastMessage;
        private LocalDateTime lastMessageTime;
        private int unreadCount;

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder otherUserId(Long otherUserId) {
            this.otherUserId = otherUserId;
            return this;
        }

        public Builder otherUserName(String otherUserName) {
            this.otherUserName = otherUserName;
            return this;
        }

        public Builder otherUserRole(String otherUserRole) {
            this.otherUserRole = otherUserRole;
            return this;
        }

        public Builder lastMessage(String lastMessage) {
            this.lastMessage = lastMessage;
            return this;
        }

        public Builder lastMessageTime(LocalDateTime lastMessageTime) {
            this.lastMessageTime = lastMessageTime;
            return this;
        }

        public Builder unreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
            return this;
        }

        public ConversationPreviewResponse build() {
            ConversationPreviewResponse obj = new ConversationPreviewResponse();
            obj.chatId = this.chatId;
            obj.otherUserId = this.otherUserId;
            obj.otherUserName = this.otherUserName;
            obj.otherUserRole = this.otherUserRole;
            obj.lastMessage = this.lastMessage;
            obj.lastMessageTime = this.lastMessageTime;
            obj.unreadCount = this.unreadCount;
            return obj;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
