package com.project.tesi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Formato standard della risposta di errore.
 * Ci garantisce che il frontend riceva sempre la stessa struttura JSON quando qualcosa esplode,
 * ignorando i campi nulli per tenere il payload leggero.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /** Data e ora in cui si è verificato l'errore. */
    private LocalDateTime timestamp;

    /** Codice HTTP numerico (es. 404, 500). */
    private int status;

    /** Descrizione testuale dello status HTTP (es. "Not Found", "Internal Server Error"). */
    private String error;

    /** Messaggio leggibile che descrive l'errore specifico. */
    private String message;

    /** Path dell'endpoint che ha generato l'errore (es. "/api/bookings"). */
    private String path;

    /** Mappa campo → messaggio di errore, presente solo per errori di validazione @Valid. */
    private Map<String, String> validationErrors;

    private ErrorResponse() {}

    public static class Builder {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> validationErrors;

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder validationErrors(Map<String, String> validationErrors) {
            this.validationErrors = validationErrors;
            return this;
        }

        public ErrorResponse build() {
            ErrorResponse obj = new ErrorResponse();
            obj.timestamp = this.timestamp;
            obj.status = this.status;
            obj.error = this.error;
            obj.message = this.message;
            obj.path = this.path;
            obj.validationErrors = this.validationErrors;
            return obj;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
