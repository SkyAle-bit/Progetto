package com.project.tesi.service.impl;

import com.project.tesi.dto.request.SendMessageRequest;
import com.project.tesi.dto.response.ChatMessageResponse;
import com.project.tesi.dto.response.ConversationPreviewResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.chat.ChatNotAllowedException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.mapper.ChatMessageMapper;
import com.project.tesi.model.ChatMessage;
import com.project.tesi.model.User;
import com.project.tesi.repository.ChatMessageRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio di messaggistica chat.
 *
 * Gestisce l'invio di messaggi (REST e WebSocket), il recupero delle conversazioni,
 * la marcatura come letti e il conteggio dei non letti.
 * Applica una regola di business: la chat è consentita solo tra
 * un cliente e un professionista a lui assegnato, oppure con l'Admin.
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMessageMapper chatMessageMapper;

    @Override
    @Transactional
    public ChatMessageResponse sendMessage(SendMessageRequest request) {
        if (request.getSenderId().equals(request.getReceiverId())) {
            throw new IllegalArgumentException("Non puoi inviare un messaggio a te stesso");
        }

        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new ResourceNotFoundException("Mittente", request.getSenderId()));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinatario", request.getReceiverId()));

        // Regola business: solo client ↔ professionista assegnato
        validateChatPermission(sender, receiver);

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(request.getContent())
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        return chatMessageMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void sendMessageDirect(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);
        if (sender == null || receiver == null)
            return;

        ChatMessage message = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build();
        chatMessageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversation(Long userId1, Long userId2, int page, int size) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", userId1));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", userId2));

        // Regola business: solo client ↔ professionista assegnato
        validateChatPermission(user1, user2);

        List<ChatMessage> messages = chatMessageRepository.findConversation(
                userId1, userId2, PageRequest.of(page, size));

        return messages.stream()
                .map(chatMessageMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationPreviewResponse> getUserConversations(Long userId) {
        if (!userRepository.existsById(userId)) {
            return java.util.Collections.emptyList();
        }

        List<User> partners = chatMessageRepository.findConversationPartners(userId);

        return partners.stream()
                .map(partner -> {
                    List<ChatMessage> lastMsgs = chatMessageRepository.findLastMessages(userId, partner.getId(),
                            PageRequest.of(0, 1));
                    ChatMessage lastMsg = lastMsgs.isEmpty() ? null : lastMsgs.get(0);
                    int unread = chatMessageRepository.countUnreadMessages(userId, partner.getId());
                    return chatMessageMapper.toConversationPreview(partner, lastMsg, unread);
                })
                .sorted((a, b) -> {
                    if (a.getLastMessageTime() == null)
                        return 1;
                    if (b.getLastMessageTime() == null)
                        return -1;
                    return b.getLastMessageTime().compareTo(a.getLastMessageTime());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long receiverId, Long senderId) {
        if (!userRepository.existsById(receiverId) || !userRepository.existsById(senderId)) {
            return;
        }

        chatMessageRepository.markMessagesAsRead(receiverId, senderId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getTotalUnreadCount(Long userId) {
        if (!userRepository.existsById(userId))
            return 0;
        return chatMessageRepository.countAllUnreadMessages(userId);
    }

    // ── Validazione permessi chat ──────────────────────────────────────────────

    private void validateChatPermission(User userA, User userB) {
        // Admin può chattare con chiunque
        if (userA.getRole() == Role.ADMIN || userB.getRole() == Role.ADMIN) {
            return;
        }

        // Insurance Manager può chattare con Admin (già coperto sopra) — blocca tutto
        // il resto
        if (userA.getRole() == Role.INSURANCE_MANAGER || userB.getRole() == Role.INSURANCE_MANAGER) {
            throw new ChatNotAllowedException(
                    "L'account polizze può comunicare solo con l'amministratore.");
        }

        // Client ↔ Professionista assegnato
        User client;
        User professional;

        if (userA.getRole() == Role.CLIENT && isProfessional(userB)) {
            client = userA;
            professional = userB;
        } else if (userB.getRole() == Role.CLIENT && isProfessional(userA)) {
            client = userB;
            professional = userA;
        } else {
            throw new ChatNotAllowedException();
        }

        // Verifica che il professionista sia effettivamente assegnato al cliente
        boolean isAssigned = false;
        if (professional.getRole() == Role.PERSONAL_TRAINER
                && client.getAssignedPT() != null
                && client.getAssignedPT().getId().equals(professional.getId())) {
            isAssigned = true;
        }
        if (professional.getRole() == Role.NUTRITIONIST
                && client.getAssignedNutritionist() != null
                && client.getAssignedNutritionist().getId().equals(professional.getId())) {
            isAssigned = true;
        }

        if (!isAssigned) {
            throw new ChatNotAllowedException(
                    "Non puoi comunicare con questo professionista: non è assegnato a te.");
        }
    }

    private boolean isProfessional(User user) {
        return user.getRole() == Role.PERSONAL_TRAINER || user.getRole() == Role.NUTRITIONIST;
    }

    @Override
    @Transactional(readOnly = true)
    public String getUserFullName(Long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse("Utente");
    }
}
