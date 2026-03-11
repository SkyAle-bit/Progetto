package com.project.tesi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO di risposta per lo stato dell'abbonamento di un cliente.
 * Mostra il piano attivo, le date di validità e i crediti residui
 * per prenotare consulenze con PT e Nutrizionisti.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {

    /** ID dell'abbonamento. */
    private Long id;

    /** Nome del piano sottoscritto (es. "Gold Annuale", "Silver Semestrale"). */
    private String planName;

    /** Data di inizio dell'abbonamento. */
    private LocalDate startDate;

    /** Data di scadenza dell'abbonamento. */
    private LocalDate endDate;

    /** Indica se l'abbonamento è attualmente attivo. */
    private boolean isActive;

    /** Crediti residui per prenotazioni con il Personal Trainer (reset mensile). */
    private int remainingPtCredits;

    /** Crediti residui per prenotazioni con il Nutrizionista (reset mensile). */
    private int remainingNutritionistCredits;
}