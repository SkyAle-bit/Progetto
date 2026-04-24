package com.project.tesi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private User professional;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private boolean isBooked;

    /**
     * Versione per Optimistic Locking.
     * Impedisce che due clienti prenotino lo stesso slot contemporaneamente:
     * se la versione in DB non corrisponde a quella letta, viene lanciata
     * un'eccezione {@link jakarta.persistence.OptimisticLockException}.
     */
    @Version
    private Integer version;

    public static com.project.tesi.builder.SlotBuilder builder() {
        return new com.project.tesi.builder.impl.SlotBuilderImpl();
    }

}