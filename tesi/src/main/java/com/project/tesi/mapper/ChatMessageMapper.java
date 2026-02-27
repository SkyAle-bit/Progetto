package com.project.tesi.mapper;

import com.project.tesi.dto.response.ChatMessageResponse;
import com.project.tesi.dto.response.ConversationPreviewResponse;
import com.project.tesi.model.ChatMessage;
import com.project.tesi.model.User;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageMapper {

    public ChatMessageResponse toResponse(ChatMessage message) {
        if (message == null) return null;

        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .receiverId(message.getReceiver().getId())
                .receiverName(message.getReceiver().getFirstName() + " " + message.getReceiver().getLastName())
                .content(message.getContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public ConversationPreviewResponse toConversationPreview(User otherUser, ChatMessage lastMessage, int unreadCount) {
        if (otherUser == null) return null;

        return ConversationPreviewResponse.builder()
                .otherUserId(otherUser.getId())
                .otherUserName(otherUser.getFirstName() + " " + otherUser.getLastName())
                .otherUserRole(otherUser.getRole().name())
                .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null)
                .unreadCount(unreadCount)
                .build();
    }
}

