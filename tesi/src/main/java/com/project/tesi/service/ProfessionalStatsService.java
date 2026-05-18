package com.project.tesi.service;

import org.springframework.validation.annotation.Validated;

import com.project.tesi.dto.response.stats.ProfessionalStatsResponse;

/**
 * Interfaccia del servizio per le statistiche della dashboard del professionista.
 * Calcola metriche come clienti assegnati, appuntamenti di oggi, documenti caricati.
 */
@Validated
public interface ProfessionalStatsService {

    /** Restituisce le statistiche aggregate per la dashboard del professionista. */
    ProfessionalStatsResponse getProfessionalStats(Long professionalId);
}
