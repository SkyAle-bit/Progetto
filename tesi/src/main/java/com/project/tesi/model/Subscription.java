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

    /** Identificativo univoco dell'abbonamento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Cliente titolare dell'abbonamento (relazione 1:1). */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Piano commerciale sottoscritto (caricato EAGER perché usato spesso). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    // ── STATO PAGAMENTO ─────────────────────────────────────────

    /** Modalità di pagamento scelta: UNICA_SOLUZIONE o RATE_MENSILI. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentFrequency paymentFrequency;

    /** Numero di rate effettivamente pagate finora. */
    private int installmentsPaid;

    /** Numero totale di rate previste (1 per soluzione unica, 6 o 12 per rate). */
    private int totalInstallments;

    /** Data del prossimo pagamento atteso ({@code null} se pagato in soluzione unica). */
    private LocalDate nextPaymentDate;

    // ── DURATA TEMPORALE ────────────────────────────────────────

    /** Data di attivazione dell'abbonamento. */
    private LocalDate startDate;

    /** Data di scadenza dell'abbonamento. */
    private LocalDate endDate;

    /** Indica se l'abbonamento è attualmente attivo. */
    private boolean active;

    // ── SALDO CREDITI CORRENTE ──────────────────────────────────

    /** Crediti residui per prenotazioni con il Personal Trainer (reset mensile). */
    private int currentCreditsPT;

    /** Crediti residui per prenotazioni con il Nutrizionista (reset mensile). */
    private int currentCreditsNutri;

    /** Data dell'ultimo reset mensile dei crediti (usata dallo scheduler). */
    private LocalDate lastRenewalDate;
}