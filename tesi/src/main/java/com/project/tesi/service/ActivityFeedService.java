package com.project.tesi.service;

import com.project.tesi.model.Booking;

import java.util.List;
import java.util.Map;

/**
 * Interfaccia del servizio per il feed delle attività recenti.
 * Restituisce prenotazioni e documenti caricati negli ultimi giorni,
 * differenziando il contenuto in base al ruolo dell'utente (CLIENT o professionista).
 */
public interface ActivityFeedService {

    /**
     * Restituisce il feed delle attività recenti di un utente.
     *
     * @param userId ID dell'utente
     * @param days   numero di giorni da considerare
     * @param limit  numero massimo di attività
     * @return lista di attività ordinate dalla più recente
     */
    List<Map<String, Object>> getActivityFeed(Long userId, int days, int limit);

    /**
     * Registra nel layer di persistenza la creazione di una nuova prenotazione.
     *
     * <p>Questo metodo è invocato dal {@code ActivityFeedUpdateListener} (pattern Observer)
     * al momento della notifica dell'evento {@code BOOKING_CREATED}. Assicura che il listener
     * non contenga codice morto, ma deleghi al service la responsabilità di aggiornare
     * lo stato persistente dell'entità {@link Booking}, rispettando il principio
     * di separazione dei layer.</p>
     *
     * @param booking la prenotazione appena creata
     */
    void logBookingCreated(Booking booking);
}
