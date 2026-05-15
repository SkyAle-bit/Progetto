package com.project.tesi.service;

import com.project.tesi.dto.response.stats.AdminStatsResponse;

/**
 * Interfaccia del servizio per le statistiche del pannello Admin.
 * Calcola metriche aggregate: distribuzione utenti, fatturato, crediti, prenotazioni.
 */
public interface AdminStatsService {

    /** Restituisce tutte le statistiche aggregate per la dashboard admin. */
    AdminStatsResponse getAdminStats();
}
