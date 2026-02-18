package com.project.tesi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PlanDuration {
    SEMESTRALE(6),
    ANNUALE(12);

    private final int months; // Utile per calcolare la data di fine
}