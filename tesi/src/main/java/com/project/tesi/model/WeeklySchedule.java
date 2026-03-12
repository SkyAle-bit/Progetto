package com.project.tesi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Entità Programmazione Settimanale — definisce una fascia oraria ricorrente
 * nel calendario di un professionista (PT o Nutrizionista).
 *
 * Ogni record indica che il professionista è disponibile in un certo
 * giorno della settimana (es. MONDAY) dalle {@code startTime} alle {@code endTime}.
 *
 * Lo scheduler automatico ({@code SlotServiceImpl.generateSlotsFromSchedule})
 * legge queste regole per generare gli {@link Slot} di 30 minuti
 * per la settimana successiva.
 *
 * Esempio: se un PT ha una regola MONDAY 09:00–13:00, verranno generati
 * 8 slot da 30 minuti per ogni lunedì futuro nel range richiesto.
 */
@Entity
@Table(name = "weekly_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySchedule {

    /** Identificativo univoco della regola oraria. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Professionista a cui appartiene questa fascia oraria ricorrente. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private User professional;

    /** Giorno della settimana (es. MONDAY, TUESDAY, ..., SUNDAY). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    /** Ora di inizio della disponibilità (es. 09:00). */
    @Column(nullable = false)
    private LocalTime startTime;

    /** Ora di fine della disponibilità (es. 13:00). */
    @Column(nullable = false)
    private LocalTime endTime;
}