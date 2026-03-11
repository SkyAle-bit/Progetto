package com.project.tesi.service;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.response.SubscriptionResponse;

/**
 * Interfaccia del servizio per la gestione degli abbonamenti dei clienti.
 * Permette l'attivazione di un nuovo piano e la consultazione dello stato attuale.
 */
public interface SubscriptionService {

    /** Attiva un nuovo abbonamento per il cliente. */
    SubscriptionResponse activateSubscription(PlanRequest request);

    /** Restituisce lo stato dell'abbonamento attivo (crediti, scadenza, piano). */
    SubscriptionResponse getSubscriptionStatus(Long userId);
}