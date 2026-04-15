package com.project.tesi.model;

import com.project.tesi.enums.PaymentFrequency;
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
import java.time.LocalDate;

/**
 * Entità Abbonamento — rappresenta la sottoscrizione attiva di un cliente
 * a un determinato {@link Plan}.
 *
 * Gestisce:
 * <ul>
 *   <li><b>Stato pagamento</b> — frequenza (unica soluzione o rate mensili),
 *       rate pagate e prossima scadenza</li>
 *   <li><b>Durata temporale</b> — data di inizio e fine dell'abbonamento</li>
 *   <li><b>Saldo crediti</b> — crediti residui per prenotare consulenze con PT e Nutrizionisti,
 *       rinnovati mensilmente dallo scheduler {@code SubscriptionScheduler}</li>
 * </ul>
 *
 * Ogni utente può avere al massimo <b>un solo abbonamento attivo</b> alla volta
 * (campo {@code active = true}). L'attivazione di un nuovo piano disattiva il precedente.
 */
@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentFrequency paymentFrequency;

    private int installmentsPaid;

    private int totalInstallments;

    private LocalDate nextPaymentDate;


    private LocalDate startDate;

    private LocalDate endDate;

    private boolean active;


    private int currentCreditsPT;

    private int currentCreditsNutri;

    private LocalDate lastRenewalDate;
}