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
 * Entità Recensione — rappresenta una valutazione lasciata da un cliente
 * a un professionista (PT o Nutrizionista).
 *
 * Regole di business:
 * <ul>
 *   <li>Un cliente può lasciare <b>una sola recensione</b> per ogni professionista
 *       (vincolo di unicità sulla coppia {@code client_id + professional_id}).</li>
 *   <li>La recensione è consentita solo dopo almeno 1 mese dalla registrazione del cliente.</li>
 * </ul>
 */
@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"client_id", "professional_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    /** Identificativo univoco della recensione. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Cliente autore della recensione. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    /** Professionista recensito. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private User professional;

    /** Voto numerico da 1 (pessimo) a 5 (eccellente). */
    @Column(nullable = false)
    private int rating;

    /** Commento testuale opzionale (massimo 1000 caratteri). */
    @Column(length = 1000)
    private String comment;

    /** Data e ora di creazione della recensione (impostata automaticamente). */
    @CreationTimestamp
    private LocalDateTime createdAt;
}