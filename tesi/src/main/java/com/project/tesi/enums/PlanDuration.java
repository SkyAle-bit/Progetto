package com.project.tesi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Durata di un piano di abbonamento.
 * Ogni valore ha associato il numero di mesi corrispondente,
 * usato per calcolare automaticamente la data di scadenza.
 */
@Getter
@RequiredArgsConstructor
public enum PlanDuration {
    /** Piano semestrale — 6 mesi. */
    SEMESTRALE(6),
    /** Piano annuale — 12 mesi. */
    ANNUALE(12);

    /** Numero di mesi di durata del piano. */
    private final int months;
}