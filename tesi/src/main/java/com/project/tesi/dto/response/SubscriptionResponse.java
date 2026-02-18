package com.project.tesi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionResponse {
    private Long id;
    private String planName; // "Basic Pack" o "Premium Pack"
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;

    // Crediti residui correnti
    private int remainingPtCredits;
    private int remainingNutritionistCredits;
}