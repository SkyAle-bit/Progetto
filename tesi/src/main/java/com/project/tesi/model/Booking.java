package com.project.tesi.model;

import com.project.tesi.enums.BookingStatus;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Entità Prenotazione — rappresenta un appuntamento prenotato da un cliente
 * con un professionista (PT o Nutrizionista).
 *
 * Ogni prenotazione è legata a uno {@link Slot} di 30 minuti e contiene
 * un link Jitsi Meet generato automaticamente per la videochiamata.
 *
 * La relazione Slot ↔ Booking è 1:1 (uno slot può avere al massimo
 * una prenotazione, vincolo {@code unique = true}).
 */
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    /** Identificativo univoco della prenotazione. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Cliente che ha effettuato la prenotazione. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Professionista con cui è stato prenotato l'appuntamento. */
    @ManyToOne
    @JoinColumn(name = "professional_id")
    private User professional;

    /** Slot temporale prenotato (relazione 1:1, uno slot = una sola prenotazione). */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false, unique = true)
    private Slot slot;

    /** Stato corrente della prenotazione (CONFIRMED, CANCELLED, COMPLETED). */
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    /** Data e ora in cui la prenotazione è stata creata (impostata automaticamente). */
    @CreationTimestamp
    private LocalDateTime bookedAt;

    /** Link alla videochiamata Jitsi Meet, generato automaticamente alla creazione. */
    @Column(nullable = false)
    private String meetingLink;

    /** Indica se l'email di promemoria (30 min prima) è già stata inviata. */
    @Builder.Default
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean reminderSent = false;
}