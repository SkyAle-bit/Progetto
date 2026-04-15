package com.project.tesi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entità che traccia la terminazione di una chat da parte di un utente.
 *
 * Quando un utente termina una conversazione con un operatore di supporto,
 * la chat scompare dalla sua lista ma rimane visibile per l'operatore
 * con un'indicazione che l'utente ha terminato la conversazione.
 */
@Entity
@Table(name = "chat_terminations", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "terminated_by_id", "other_user_id" })
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatTermination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminated_by_id", nullable = false)
    private User terminatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_user_id", nullable = false)
    private User otherUser;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime terminatedAt;
}
