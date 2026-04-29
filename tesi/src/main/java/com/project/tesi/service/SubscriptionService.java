package com.project.tesi.service;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.model.Booking;

/**
 * Interfaccia del servizio per la gestione degli abbonamenti dei clienti.
 * Permette l'attivazione di un nuovo piano e la consultazione dello stato attuale.
 *
 * <p>Espone inoltre i metodi {@link #deductCredits(Booking)} e {@link #refundCredits(Booking)}
 * per incapsulare la logica di consumo e rimborso dei crediti, evitando che i listener
 * del pattern Observer accedano direttamente al repository (violazione del layer).</p>
 */
public interface SubscriptionService {

    /** Attiva un nuovo abbonamento per il cliente. */
    SubscriptionResponse activateSubscription(PlanRequest request);

    /** Restituisce lo stato dell'abbonamento attivo (crediti, scadenza, piano). */
    SubscriptionResponse getSubscriptionStatus(Long userId);

    /**
     * Scala i crediti dell'abbonamento attivo dell'utente in base al ruolo del professionista.
     *
     * <p>Invocato dal {@code CreditDeductionListener} (pattern Observer) dopo la creazione
     * di una prenotazione. Incapsula la logica di selezione della strategy e la persistenza,
     * rispettando il principio di separazione dei layer: i listener non devono accedere
     * direttamente al repository.</p>
     *
     * @param booking la prenotazione per cui scalare i crediti
     */
    void deductCredits(Booking booking);

    /**
     * Rimborsa i crediti dell'abbonamento attivo dell'utente in base al ruolo del professionista.
     *
     * <p>Invocato dal {@code CreditRefundListener} (pattern Observer) dopo l'annullamento
     * di una prenotazione. Incapsula la logica di selezione della strategy e la persistenza.</p>
     *
     * @param booking la prenotazione annullata per cui rimborsare i crediti
     */
    void refundCredits(Booking booking);
}