package com.project.tesi.service;

import java.util.Map;

/**
 * Interfaccia del servizio per le statistiche del pannello Admin.
 * Calcola metriche aggregate: distribuzione utenti, fatturato, crediti, prenotazioni.
 */
public interface AdminStatsService {

    /** Restituisce tutte le statistiche aggregate per la dashboard admin. */
    Map<String, Object> getAdminStats();
}
