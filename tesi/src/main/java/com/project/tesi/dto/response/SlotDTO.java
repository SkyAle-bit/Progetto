package com.project.tesi.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO di risposta per uno slot del calendario di un professionista.
 * Usato sia in lettura (calendario disponibilità) che in scrittura
 * (creazione manuale di nuovi slot da parte del professionista).
 *
 * <p>Implementa manualmente il Design Pattern <b>Builder</b> tramite la classe
 * statica interna {@link Builder}, garantendo costruzione fluente e controllata
 * senza dipendere dalla generazione automatica di Lombok.</p>
 */
@Data
public class SlotDTO {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isAvailable;
    private Long professionalId;

    // Costruttore privato: la creazione è delegata al Builder
    private SlotDTO() {}

    // ──────────────────────────────────────────────────
    //  Builder interno statico — Design Pattern Builder
    // ──────────────────────────────────────────────────

    /**
     * Builder interno per la costruzione fluente di {@link SlotDTO}.
     */
    public static class Builder {

        private Long id;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private boolean isAvailable;
        private Long professionalId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder isAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public Builder professionalId(Long professionalId) {
            this.professionalId = professionalId;
            return this;
        }

        /**
         * Costruisce e restituisce l'istanza di {@link SlotDTO}.
         *
         * @return una nuova istanza con i valori impostati tramite il Builder
         */
        public SlotDTO build() {
            SlotDTO dto = new SlotDTO();
            dto.id = this.id;
            dto.startTime = this.startTime;
            dto.endTime = this.endTime;
            dto.isAvailable = this.isAvailable;
            dto.professionalId = this.professionalId;
            return dto;
        }
    }

    /** Punto di ingresso statico per la costruzione fluente. */
    public static Builder builder() {
        return new Builder();
    }
}