package com.project.tesi.service;

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
}
