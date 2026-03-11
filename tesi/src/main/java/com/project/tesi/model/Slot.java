package com.project.tesi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entità Slot — rappresenta una fascia oraria di 30 minuti disponibile
 * nel calendario di un professionista (PT o Nutrizionista).
 *
 * Gli slot vengono generati automaticamente dallo scheduler settimanale
 * a partire dalle regole orarie definite in {@link WeeklySchedule},
 * oppure creati manualmente dal professionista.
 *
 * Il campo {@code version} implementa l'<b>Optimistic Locking</b> di JPA
 * per prevenire la doppia prenotazione concorrente dello stesso slot.
 *
 * Indici sul database:
 * <ul>
 *   <li>{@code idx_slot_time} — ottimizza le ricerche per data/ora di inizio</li>
 *   <li>{@code idx_slot_prof} — ottimizza le ricerche per professionista</li>
 * </ul>
 */
@Entity
@Table(name = "slots", indexes = {
        @Index(name = "idx_slot_time", columnList = "startTime"),
        @Index(name = "idx_slot_prof", columnList = "professional_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slot {

    /** Identificativo univoco dello slot. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Professionista proprietario dello slot. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private User professional;

    /** Data e ora di inizio della fascia oraria. */
    @Column(nullable = false)
    private LocalDateTime startTime;

    /** Data e ora di fine della fascia oraria (startTime + 30 minuti). */
    @Column(nullable = false)
    private LocalDateTime endTime;

    /** Indica se lo slot è già stato prenotato da un cliente ({@code true} = occupato). */
    private boolean isBooked;

    /**
     * Versione per Optimistic Locking.
     * Impedisce che due clienti prenotino lo stesso slot contemporaneamente:
     * se la versione in DB non corrisponde a quella letta, viene lanciata
     * un'eccezione {@link jakarta.persistence.OptimisticLockException}.
     */
    @Version
    private Integer version;
}