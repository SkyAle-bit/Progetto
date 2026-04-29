package com.project.tesi.dto.response;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO di risposta per lo stato dell'abbonamento di un cliente.
 * Mostra il piano attivo, le date di validità e i crediti residui
 * per prenotare consulenze con PT e Nutrizionisti.
 *
 * <p>Implementa manualmente il Design Pattern <b>Builder</b> tramite la classe
 * statica interna {@link Builder}, garantendo costruzione fluente e controllata
 * senza dipendere dalla generazione automatica di Lombok.</p>
 */
@Data
public class SubscriptionResponse {

    private Long id;
    private String planName;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;
    private int remainingPtCredits;
    private int remainingNutritionistCredits;

    // Costruttore privato: la creazione è delegata al Builder
    private SubscriptionResponse() {}

    // ──────────────────────────────────────────────────
    //  Builder interno statico — Design Pattern Builder
    // ──────────────────────────────────────────────────

    /**
     * Builder interno per la costruzione fluente di {@link SubscriptionResponse}.
     */
    public static class Builder {

        private Long id;
        private String planName;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean isActive;
        private int remainingPtCredits;
        private int remainingNutritionistCredits;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder planName(String planName) {
            this.planName = planName;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder remainingPtCredits(int remainingPtCredits) {
            this.remainingPtCredits = remainingPtCredits;
            return this;
        }

        public Builder remainingNutritionistCredits(int remainingNutritionistCredits) {
            this.remainingNutritionistCredits = remainingNutritionistCredits;
            return this;
        }

        /**
         * Costruisce e restituisce l'istanza di {@link SubscriptionResponse}.
         *
         * @return una nuova istanza con i valori impostati tramite il Builder
         */
        public SubscriptionResponse build() {
            SubscriptionResponse response = new SubscriptionResponse();
            response.id = this.id;
            response.planName = this.planName;
            response.startDate = this.startDate;
            response.endDate = this.endDate;
            response.isActive = this.isActive;
            response.remainingPtCredits = this.remainingPtCredits;
            response.remainingNutritionistCredits = this.remainingNutritionistCredits;
            return response;
        }
    }

    /** Punto di ingresso statico per la costruzione fluente. */
    public static Builder builder() {
        return new Builder();
    }
}