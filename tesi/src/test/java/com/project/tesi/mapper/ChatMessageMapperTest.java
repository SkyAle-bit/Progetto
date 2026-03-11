package com.project.tesi.mapper;

import com.project.tesi.dto.response.ChatMessageResponse;
import com.project.tesi.dto.response.ConversationPreviewResponse;
import com.project.tesi.enums.MessageStatus;
import com.project.tesi.enums.Role;
import com.project.tesi.model.ChatMessage;
import com.project.tesi.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitari per {@link ChatMessageMapper}.
 */
class ChatMessageMapperTest {

    private final ChatMessageMapper mapper = new ChatMessageMapper();

    @Test
    @DisplayName("toResponse — converte correttamente un ChatMessage nel DTO")
    void toResponse_success() {
        User sender = User.builder().id(1L).firstName("Mario").lastName("Rossi").build();
        User receiver = User.builder().id(2L).firstName("Luca").lastName("Bianchi").build();
        LocalDateTime now = LocalDateTime.now();

        ChatMessage msg = ChatMessage.builder()
                .id(1L).sender(sender).receiver(receiver)
                .content("Ciao!").status(MessageStatus.SENT).createdAt(now).build();

        ChatMessageResponse resp = mapper.toResponse(msg);

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isEqualTo(1L);
        assertThat(resp.getSenderId()).isEqualTo(1L);
        assertThat(resp.getSenderName()).isEqualTo("Mario Rossi");
        assertThat(resp.getReceiverId()).isEqualTo(2L);
        assertThat(resp.getContent()).isEqualTo("Ciao!");
        assertThat(resp.getStatus()).isEqualTo(MessageStatus.SENT);
    }

    @Test
    @DisplayName("toResponse — null input restituisce null")
    void toResponse_null() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("toConversationPreview — costruisce anteprima con ultimo messaggio")
    void toConversationPreview_withMessage() {
        User other = User.builder().id(2L).firstName("Luca").lastName("Bianchi").role(Role.PERSONAL_TRAINER).build();
        LocalDateTime now = LocalDateTime.now();
        ChatMessage lastMsg = ChatMessage.builder().content("Ultima risposta").createdAt(now).build();

        ConversationPreviewResponse resp = mapper.toConversationPreview(other, lastMsg, 3);

        assertThat(resp).isNotNull();
        assertThat(resp.getOtherUserId()).isEqualTo(2L);
        assertThat(resp.getOtherUserName()).isEqualTo("Luca Bianchi");
        assertThat(resp.getLastMessage()).isEqualTo("Ultima risposta");
        assertThat(resp.getUnreadCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("toConversationPreview — senza ultimo messaggio")
    void toConversationPreview_withoutMessage() {
        User other = User.builder().id(2L).firstName("Luca").lastName("Bianchi").role(Role.CLIENT).build();

        ConversationPreviewResponse resp = mapper.toConversationPreview(other, null, 0);

        assertThat(resp).isNotNull();
        assertThat(resp.getLastMessage()).isNull();
        assertThat(resp.getLastMessageTime()).isNull();
        assertThat(resp.getUnreadCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("toConversationPreview — null otherUser restituisce null")
    void toConversationPreview_nullUser() {
        assertThat(mapper.toConversationPreview(null, null, 0)).isNull();
    }
}

