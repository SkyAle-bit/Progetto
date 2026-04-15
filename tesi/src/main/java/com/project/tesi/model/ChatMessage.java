package com.project.tesi.model;

import com.project.tesi.enums.MessageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entità Messaggio Chat — rappresenta un singolo messaggio scambiato
 * tra due utenti nella chat in tempo reale.
 *
 * I messaggi vengono inviati via WebSocket per l'interazione real-time
 * e persistiti su database per lo storico delle conversazioni.
 *
 * Un indice composito su {@code (sender_id, receiver_id, created_at)}
 * ottimizza le query di recupero della cronologia messaggi.
 */
@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name = "idx_chat_sender_receiver", columnList = "sender_id, receiver_id, created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
