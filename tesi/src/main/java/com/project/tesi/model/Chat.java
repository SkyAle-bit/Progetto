package com.project.tesi.model;

import com.project.tesi.builder.ChatBuilder;
import com.project.tesi.builder.impl.ChatBuilderImpl;
import com.project.tesi.enums.ChatStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats", uniqueConstraints = {
        @UniqueConstraint(name = "uq_chat_users", columnNames = {"user1_id", "user2_id"})
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user1", "user2", "messages"})
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false, foreignKey = @ForeignKey(name = "fk_chat_user1_id"))
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false, foreignKey = @ForeignKey(name = "fk_chat_user2_id"))
    private User user2;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatStatus status = ChatStatus.OPEN;

    private LocalDateTime closedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "closed_by_id", foreignKey = @ForeignKey(name = "fk_chat_closed_by_id"))
    private User closedBy;

    public static ChatBuilder builder() {
        return new ChatBuilderImpl();
    }
}
