package com.project.tesi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Struttura standard per tutte le risposte di errore dell'API.
 * I campi {@code null} vengono esclusi dalla serializzazione JSON
 * grazie a {@code @JsonInclude(NON_NULL)}.
 */
@Data
@Builder
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
}
