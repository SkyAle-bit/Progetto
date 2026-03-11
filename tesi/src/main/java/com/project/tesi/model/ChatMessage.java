package com.project.tesi.model;

import com.project.tesi.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.*;
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

    /** Identificativo univoco del messaggio. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Utente che ha inviato il messaggio. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /** Utente destinatario del messaggio. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    /** Contenuto testuale del messaggio (massimo 2000 caratteri). */
    @Column(nullable = false, length = 2000)
    private String content;

    /** Stato del messaggio: SENT (inviato) o READ (letto dal destinatario). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;

    /** Data e ora di invio del messaggio (impostata automaticamente, non modificabile). */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
