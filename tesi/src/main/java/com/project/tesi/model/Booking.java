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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "professional_id")
    private User professional;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @CreationTimestamp
    private LocalDateTime bookedAt;

    @Column(nullable = false)
    private String meetingLink;

    @Builder.Default
    private boolean reminderSent = false;
}