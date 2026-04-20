package com.project.tesi.service.impl;

import com.project.tesi.dto.request.SendMessageRequest;
import com.project.tesi.dto.response.ChatMessageResponse;
import com.project.tesi.dto.response.ConversationPreviewResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.chat.ChatNotAllowedException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.model.Chat;
import com.project.tesi.model.Message;
import com.project.tesi.model.User;
import com.project.tesi.repository.ChatRepository;
import com.project.tesi.repository.MessageRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long createChat(Long senderId, Long receiverId) {
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Non puoi avviare una chat con te stesso");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Mittente", senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Destinatario", receiverId));

        validateChatPermission(sender, receiver);

        Chat chat = getOrCreateChat(sender, receiver);
        return chat.getId();
    }

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(SendMessageRequest request) {
        Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat", request.getChatId()));

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Mittente", request.getSenderId()));

        if (!chat.getUser1().getId().equals(sender.getId()) && !chat.getUser2().getId().equals(sender.getId())) {
             throw new ChatNotAllowedException("Non sei parte di questa chat");
        }

        Message message = Message.builder()
                .chat(chat)
                .user(sender)
                .content(request.getContent())
                .timeStamp(LocalDateTime.now())
                .isRead(false)
                .build();

        Message saved = messageRepository.save(message);

        Long receiverId = chat.getUser1().getId().equals(sender.getId()) ? chat.getUser2().getId() : chat.getUser1().getId();
        return toChatMessageResponse(saved, receiverId);
    }

    @Override
    @Transactional
    public void sendMessageDirect(Long chatId, Long senderId, String content) {
        Chat chat = chatRepository.findById(chatId).orElse(null);
        User sender = userRepository.findById(senderId).orElse(null);
        if (chat == null || sender == null) return;

        if (!chat.getUser1().getId().equals(sender.getId()) && !chat.getUser2().getId().equals(sender.getId())) {
             return; // not part of chat
        }

        Message message = Message.builder()
                .chat(chat)
                .user(sender)
                .content(content)
                .timeStamp(LocalDateTime.now())
                .isRead(false)
                .build();

        messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversation(Long chatId, Long userId, int page, int size) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat", chatId));

        if (!chat.getUser1().getId().equals(userId) && !chat.getUser2().getId().equals(userId)) {
             throw new ChatNotAllowedException("Non sei parte di questa chat");
        }

        List<Message> messages = messageRepository.findMessagesByChatId(chat.getId(), PageRequest.of(page, size));

        return messages.stream()
                .map(m -> {
                     Long receiverId = chat.getUser1().getId().equals(m.getUser().getId()) ? chat.getUser2().getId() : chat.getUser1().getId();
                     return toChatMessageResponse(m, receiverId);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationPreviewResponse> getUserConversations(Long userId) {
        List<Chat> chats = chatRepository.findAllChatsByUserId(userId);
        User currentUser = userRepository.findById(userId).orElse(null);

        return chats.stream().map(chat -> {
            User partner = chat.getUser1().getId().equals(userId) ? chat.getUser2() : chat.getUser1();
            Message lastMsg = messageRepository.findLastMessageByChatId(chat.getId());
            int unreadCount = messageRepository.countUnreadMessagesByChatIdAndUserId(chat.getId(), userId);

            return ConversationPreviewResponse.builder()
                    .chatId(chat.getId())
                    .otherUserId(partner.getId())
                    .otherUserName(partner.getFirstName() + " " + partner.getLastName())
                    .otherUserRole(partner.getRole() != null ? partner.getRole().name() : null)
                    .lastMessage(lastMsg != null ? lastMsg.getContent() : "")
                    .lastMessageTime(lastMsg != null ? lastMsg.getTimeStamp() : null)
                    .unreadCount(unreadCount)
                    .build();
        })
        .filter(res -> {
            if (res.getLastMessageTime() == null && currentUser != null) {
                Role role = currentUser.getRole();
                if (role == Role.CLIENT || role == Role.PERSONAL_TRAINER || role == Role.NUTRITIONIST) {
                    if ("ADMIN".equals(res.getOtherUserRole()) || "MODERATOR".equals(res.getOtherUserRole())) {
                        return false; // hide empty admin/moderator chats for normal users
                    }
                }
            }
            return true;
        })
        .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long chatId, Long userId) {
        chatRepository.findById(chatId).ifPresent(chat -> {
            if (chat.getUser1().getId().equals(userId) || chat.getUser2().getId().equals(userId)) {
                messageRepository.markMessagesAsRead(chat.getId(), userId);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalUnreadCount(Long userId) {
        return messageRepository.countTotalUnreadMessagesByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getUserFullName(Long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse("Utente");
    }


    @Override
    @Transactional(readOnly = true)
    public Chat getChatEntity(Long chatId) {
        return chatRepository.findById(chatId).orElse(null);
    }

    private Chat getOrCreateChat(User user1, User user2) {
        return chatRepository.findChatBetweenUsers(user1.getId(), user2.getId())
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .user1(user1)
                            .user2(user2)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return chatRepository.save(newChat);
                });
    }

    private void validateChatPermission(User uA, User uB) {
        if (uA.getRole() == Role.ADMIN || uB.getRole() == Role.ADMIN) return;
        if (uA.getRole() == Role.MODERATOR || uB.getRole() == Role.MODERATOR) return;

        if (uA.getRole() == Role.INSURANCE_MANAGER || uB.getRole() == Role.INSURANCE_MANAGER) {
            throw new ChatNotAllowedException("Admin only");
        }

        boolean professionalAssigned = false;
        User client = null;
        User prof = null;
        if (uA.getRole() == Role.CLIENT) { client = uA; prof = uB; }
        else if (uB.getRole() == Role.CLIENT) { client = uB; prof = uA; }

        if (client != null && prof != null) {
            if (prof.getRole() == Role.PERSONAL_TRAINER && client.getAssignedPT() != null && client.getAssignedPT().getId().equals(prof.getId())) {
                professionalAssigned = true;
            }
            if (prof.getRole() == Role.NUTRITIONIST && client.getAssignedNutritionist() != null && client.getAssignedNutritionist().getId().equals(prof.getId())) {
                professionalAssigned = true;
            }
        }

        if (!professionalAssigned) {
            throw new ChatNotAllowedException("Non sei assegnato a questo utente");
        }
    }

    private ChatMessageResponse toChatMessageResponse(Message m, Long receiverId) {
        ChatMessageResponse dto = new ChatMessageResponse();
        dto.setId(m.getId());
        dto.setChatId(m.getChat().getId());
        dto.setSenderId(m.getUser().getId());
        dto.setSenderName(m.getUser().getFirstName() + " " + m.getUser().getLastName());
        dto.setReceiverId(receiverId);
        dto.setContent(m.getContent());
        dto.setCreatedAt(m.getTimeStamp());
        dto.setStatus(m.isRead() ? com.project.tesi.enums.MessageStatus.READ : com.project.tesi.enums.MessageStatus.SENT);
        return dto;
    }
}
