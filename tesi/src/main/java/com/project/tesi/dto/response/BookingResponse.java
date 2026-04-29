package com.project.tesi.dto.response;

import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.Role;
import lombok.Data;

/**
 * DTO di risposta per una prenotazione.
 * Usato nella dashboard del cliente per mostrare gli appuntamenti futuri
 * e nella dashboard del professionista per gli appuntamenti di oggi.
 *
 * <p>Implementa manualmente il Design Pattern <b>Builder</b> tramite la classe
 * statica interna {@link Builder}, garantendo costruzione fluente e controllata
 * senza dipendere dalla generazione automatica di Lombok.</p>
 */
@Data
public class BookingResponse {

    private Long id;
    private String date;
    private String startTime;
    private String endTime;
    private String professionalName;
    private String clientName;
    private Role professionalRole;
    private String meetingLink;
    private BookingStatus status;
    private boolean canJoin;

    // Costruttore privato: la creazione è delegata al Builder
    private BookingResponse() {}

    // ──────────────────────────────────────────────────
    //  Builder interno statico — Design Pattern Builder
    // ──────────────────────────────────────────────────

    /**
     * Builder interno per la costruzione fluente di {@link BookingResponse}.
     */
    public static class Builder {

        private Long id;
        private String date;
        private String startTime;
        private String endTime;
        private String professionalName;
        private String clientName;
        private Role professionalRole;
        private String meetingLink;
        private BookingStatus status;
        private boolean canJoin;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder startTime(String startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(String endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder professionalName(String professionalName) {
            this.professionalName = professionalName;
            return this;
        }

        public Builder clientName(String clientName) {
            this.clientName = clientName;
            return this;
        }

        public Builder professionalRole(Role professionalRole) {
            this.professionalRole = professionalRole;
            return this;
        }

        public Builder meetingLink(String meetingLink) {
            this.meetingLink = meetingLink;
            return this;
        }

        public Builder status(BookingStatus status) {
            this.status = status;
            return this;
        }

        public Builder canJoin(boolean canJoin) {
            this.canJoin = canJoin;
            return this;
        }

        /**
         * Costruisce e restituisce l'istanza di {@link BookingResponse}.
         *
         * @return una nuova istanza con i valori impostati tramite il Builder
         */
        public BookingResponse build() {
            BookingResponse response = new BookingResponse();
            response.id = this.id;
            response.date = this.date;
            response.startTime = this.startTime;
            response.endTime = this.endTime;
            response.professionalName = this.professionalName;
            response.clientName = this.clientName;
            response.professionalRole = this.professionalRole;
            response.meetingLink = this.meetingLink;
            response.status = this.status;
            response.canJoin = this.canJoin;
            return response;
        }
    }

    /** Punto di ingresso statico per la costruzione fluente. */
    public static Builder builder() {
        return new Builder();
    }
}