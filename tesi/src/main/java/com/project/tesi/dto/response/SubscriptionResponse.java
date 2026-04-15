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

    private Long id;

    private String planName;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isActive;

    private int remainingPtCredits;

    private int remainingNutritionistCredits;
}