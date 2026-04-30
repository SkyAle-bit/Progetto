package com.project.tesi.facade;
import com.project.tesi.dto.request.SendMessageRequest;
import com.project.tesi.dto.response.ChatMessageResponse;
import com.project.tesi.dto.response.ConversationPreviewResponse;
import com.project.tesi.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
@Component
public class ChatFacade implements IChatFacade {
    private final ChatService chatService;
    @Autowired
    public ChatFacade(ChatService chatService) {
        this.chatService = chatService;
    }
    public Long createChat(Long senderId, Long receiverId) {
        return chatService.createChat(senderId, receiverId);
    }
    public ChatMessageResponse sendMessage(SendMessageRequest request) {
        return chatService.sendMessage(request);
    }
    public List<ChatMessageResponse> getConversation(Long chatId, Long userId, int page, int size) {
        return chatService.getConversation(chatId, userId, page, size);
    }
    public List<ConversationPreviewResponse> getUserConversations(Long userId) {
        return chatService.getUserConversations(userId);
    }
    public void markAsRead(Long chatId, Long userId) {
        chatService.markAsRead(chatId, userId);
    }
    public Integer getTotalUnreadCount(Long userId) {
        return chatService.getTotalUnreadCount(userId);
    }
}
