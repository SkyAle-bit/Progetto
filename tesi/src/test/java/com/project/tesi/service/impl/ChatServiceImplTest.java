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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock private ChatMessageRepository chatMessageRepository;
    @Mock private UserRepository userRepository;
    @Mock private ChatMessageMapper chatMessageMapper;

    @InjectMocks private ChatServiceImpl chatService;

    private User client, pt, nutri, admin;

    @BeforeEach
    void setUp() {
        pt = User.builder().id(2L).firstName("Luca").lastName("Bianchi").role(Role.PERSONAL_TRAINER).build();
        nutri = User.builder().id(3L).firstName("Sara").lastName("Verdi").role(Role.NUTRITIONIST).build();
        client = User.builder().id(1L).firstName("Mario").lastName("Rossi").role(Role.CLIENT)
                .assignedPT(pt).assignedNutritionist(nutri).build();
        admin = User.builder().id(99L).firstName("Admin").lastName("Admin").role(Role.ADMIN).build();
    }

    @Test @DisplayName("sendMessage — successo client → PT assegnato")
    void sendMessage_success() {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(1L); req.setReceiverId(2L); req.setContent("Ciao");
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        ChatMessage saved = ChatMessage.builder().id(1L).sender(client).receiver(pt).content("Ciao").build();
        when(chatMessageRepository.save(any())).thenReturn(saved);
        when(chatMessageMapper.toResponse(saved)).thenReturn(ChatMessageResponse.builder().id(1L).build());

        ChatMessageResponse resp = chatService.sendMessage(req);
        assertThat(resp.getId()).isEqualTo(1L);
    }

    @Test @DisplayName("sendMessage — stessa persona lancia IllegalArgumentException")
    void sendMessage_sameUser() {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(1L); req.setReceiverId(1L); req.setContent("X");
        assertThatThrownBy(() -> chatService.sendMessage(req)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("sendMessage — mittente non trovato")
    void sendMessage_senderNotFound() {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(999L); req.setReceiverId(2L); req.setContent("X");
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> chatService.sendMessage(req)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("sendMessage — client → professionista NON assegnato → ChatNotAllowed")
    void sendMessage_notAssigned() {
        User otherPt = User.builder().id(5L).firstName("X").lastName("Y").role(Role.PERSONAL_TRAINER).build();
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(1L); req.setReceiverId(5L); req.setContent("X");
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(5L)).thenReturn(Optional.of(otherPt));
        assertThatThrownBy(() -> chatService.sendMessage(req)).isInstanceOf(ChatNotAllowedException.class);
    }

    @Test @DisplayName("sendMessage — admin può chattare con chiunque")
    void sendMessage_adminAllowed() {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(99L); req.setReceiverId(1L); req.setContent("Support");
        when(userRepository.findById(99L)).thenReturn(Optional.of(admin));
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        ChatMessage saved = ChatMessage.builder().id(1L).sender(admin).receiver(client).build();
        when(chatMessageRepository.save(any())).thenReturn(saved);
        when(chatMessageMapper.toResponse(any())).thenReturn(ChatMessageResponse.builder().id(1L).build());

        assertThatCode(() -> chatService.sendMessage(req)).doesNotThrowAnyException();
    }

    @Test @DisplayName("sendMessage — client → nutrizionista assegnato OK")
    void sendMessage_clientToNutri() {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(1L); req.setReceiverId(3L); req.setContent("Ciao");
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(3L)).thenReturn(Optional.of(nutri));
        ChatMessage saved = ChatMessage.builder().id(1L).sender(client).receiver(nutri).build();
        when(chatMessageRepository.save(any())).thenReturn(saved);
        when(chatMessageMapper.toResponse(any())).thenReturn(ChatMessageResponse.builder().id(1L).build());

        assertThatCode(() -> chatService.sendMessage(req)).doesNotThrowAnyException();
    }

    @Test @DisplayName("sendMessage — INSURANCE_MANAGER → client lancia ChatNotAllowed")
    void sendMessage_insuranceManager() {
        User ins = User.builder().id(10L).role(Role.INSURANCE_MANAGER).firstName("X").lastName("Y").build();
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(10L); req.setReceiverId(1L); req.setContent("X");
        when(userRepository.findById(10L)).thenReturn(Optional.of(ins));
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        assertThatThrownBy(() -> chatService.sendMessage(req)).isInstanceOf(ChatNotAllowedException.class);
    }

    @Test @DisplayName("sendMessage — client → client lancia ChatNotAllowed")
    void sendMessage_clientToClient() {
        User other = User.builder().id(5L).role(Role.CLIENT).firstName("A").lastName("B").build();
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(1L); req.setReceiverId(5L); req.setContent("X");
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(5L)).thenReturn(Optional.of(other));
        assertThatThrownBy(() -> chatService.sendMessage(req)).isInstanceOf(ChatNotAllowedException.class);
    }

    @Test @DisplayName("sendMessageDirect — salva messaggio direttamente")
    void sendMessageDirect_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        chatService.sendMessageDirect(1L, 2L, "Ciao");
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test @DisplayName("sendMessageDirect — utente non trovato, non salva")
    void sendMessageDirect_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        chatService.sendMessageDirect(1L, 2L, "Ciao");
        verify(chatMessageRepository, never()).save(any());
    }

    @Test @DisplayName("getConversation — restituisce lista messaggi")
    void getConversation_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        ChatMessage msg = ChatMessage.builder().id(1L).sender(client).receiver(pt).content("Hi").build();
        when(chatMessageRepository.findConversation(eq(1L), eq(2L), any(PageRequest.class))).thenReturn(List.of(msg));
        when(chatMessageMapper.toResponse(msg)).thenReturn(ChatMessageResponse.builder().id(1L).build());

        List<ChatMessageResponse> result = chatService.getConversation(1L, 2L, 0, 50);
        assertThat(result).hasSize(1);
    }

    @Test @DisplayName("getUserConversations — utente inesistente restituisce lista vuota")
    void getUserConversations_notExists() {
        when(userRepository.existsById(999L)).thenReturn(false);
        assertThat(chatService.getUserConversations(999L)).isEmpty();
    }

    @Test @DisplayName("getUserConversations — restituisce anteprime ordinate")
    void getUserConversations_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(chatMessageRepository.findConversationPartners(1L)).thenReturn(List.of(pt));
        ChatMessage lastMsg = ChatMessage.builder().content("Ciao").createdAt(LocalDateTime.now()).build();
        when(chatMessageRepository.findLastMessages(eq(1L), eq(2L), any())).thenReturn(List.of(lastMsg));
        when(chatMessageRepository.countUnreadMessages(1L, 2L)).thenReturn(3);
        when(chatMessageMapper.toConversationPreview(pt, lastMsg, 3))
                .thenReturn(ConversationPreviewResponse.builder().otherUserId(2L).lastMessageTime(LocalDateTime.now()).build());

        List<ConversationPreviewResponse> result = chatService.getUserConversations(1L);
        assertThat(result).hasSize(1);
    }

    @Test @DisplayName("markAsRead — utente esistente chiama repository")
    void markAsRead_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.existsById(2L)).thenReturn(true);
        chatService.markAsRead(1L, 2L);
        verify(chatMessageRepository).markMessagesAsRead(1L, 2L);
    }

    @Test @DisplayName("markAsRead — utente non esistente non chiama repository")
    void markAsRead_userNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        chatService.markAsRead(1L, 2L);
        verify(chatMessageRepository, never()).markMessagesAsRead(anyLong(), anyLong());
    }

    @Test @DisplayName("getTotalUnreadCount — restituisce conteggio")
    void getTotalUnreadCount() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(chatMessageRepository.countAllUnreadMessages(1L)).thenReturn(7);
        assertThat(chatService.getTotalUnreadCount(1L)).isEqualTo(7);
    }

    @Test @DisplayName("getTotalUnreadCount — utente non esistente restituisce 0")
    void getTotalUnreadCount_notExists() {
        when(userRepository.existsById(999L)).thenReturn(false);
        assertThat(chatService.getTotalUnreadCount(999L)).isEqualTo(0);
    }

    @Test @DisplayName("getUserFullName — utente trovato")
    void getUserFullName_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        assertThat(chatService.getUserFullName(1L)).isEqualTo("Mario Rossi");
    }

    @Test @DisplayName("getUserFullName — utente non trovato restituisce 'Utente'")
    void getUserFullName_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThat(chatService.getUserFullName(999L)).isEqualTo("Utente");
    }

    // ══════════════ BRANCH AGGIUNTIVE ══════════════

    @Test @DisplayName("getUserConversations — lastMessageTime null viene ordinato correttamente")
    void getUserConversations_nullLastMessageTime() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(chatMessageRepository.findConversationPartners(1L)).thenReturn(List.of(pt, nutri));

        // PT con messaggio recente
        ChatMessage lastMsg = ChatMessage.builder().content("Ciao").createdAt(LocalDateTime.now()).build();
        when(chatMessageRepository.findLastMessages(eq(1L), eq(2L), any())).thenReturn(List.of(lastMsg));
        when(chatMessageRepository.countUnreadMessages(1L, 2L)).thenReturn(0);
        when(chatMessageMapper.toConversationPreview(pt, lastMsg, 0))
                .thenReturn(ConversationPreviewResponse.builder().otherUserId(2L).lastMessageTime(LocalDateTime.now()).build());

        // Nutri senza messaggi (lastMessageTime null)
        when(chatMessageRepository.findLastMessages(eq(1L), eq(3L), any())).thenReturn(List.of());
        when(chatMessageRepository.countUnreadMessages(1L, 3L)).thenReturn(0);
        when(chatMessageMapper.toConversationPreview(nutri, null, 0))
                .thenReturn(ConversationPreviewResponse.builder().otherUserId(3L).lastMessageTime(null).build());

        List<ConversationPreviewResponse> result = chatService.getUserConversations(1L);
        assertThat(result).hasSize(2);
        // PT (con messaggio) deve essere prima di Nutri (senza messaggio)
        assertThat(result.get(0).getOtherUserId()).isEqualTo(2L);
        assertThat(result.get(1).getOtherUserId()).isEqualTo(3L);
    }

    @Test @DisplayName("sendMessageDirect — receiver null non salva")
    void sendMessageDirect_receiverNull() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        chatService.sendMessageDirect(1L, 999L, "Ciao");
        verify(chatMessageRepository, never()).save(any());
    }

    @Test @DisplayName("sendMessage — PT → client assegnato OK (direzione inversa)")
    void sendMessage_ptToClient() {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(2L); req.setReceiverId(1L); req.setContent("Ciao");
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        ChatMessage saved = ChatMessage.builder().id(1L).sender(pt).receiver(client).build();
        when(chatMessageRepository.save(any())).thenReturn(saved);
        when(chatMessageMapper.toResponse(any())).thenReturn(ChatMessageResponse.builder().id(1L).build());

        assertThatCode(() -> chatService.sendMessage(req)).doesNotThrowAnyException();
    }

    @Test @DisplayName("sendMessage — client con assignedPT null → chat con PT non assegnato lancia errore")
    void sendMessage_clientNoPtAssigned() {
        User clientNoPT = User.builder().id(10L).firstName("Anna").lastName("N").role(Role.CLIENT)
                .assignedPT(null).assignedNutritionist(null).build();
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(10L); req.setReceiverId(2L); req.setContent("Ciao");
        when(userRepository.findById(10L)).thenReturn(Optional.of(clientNoPT));
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));

        assertThatThrownBy(() -> chatService.sendMessage(req)).isInstanceOf(ChatNotAllowedException.class);
    }

    @Test @DisplayName("sendMessage — client con assignedNutritionist diverso → chat non permessa")
    void sendMessage_clientWrongNutri() {
        User otherNutri = User.builder().id(5L).firstName("X").lastName("Y").role(Role.NUTRITIONIST).build();
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(1L); req.setReceiverId(5L); req.setContent("Ciao");
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(5L)).thenReturn(Optional.of(otherNutri));

        assertThatThrownBy(() -> chatService.sendMessage(req)).isInstanceOf(ChatNotAllowedException.class);
    }
}


