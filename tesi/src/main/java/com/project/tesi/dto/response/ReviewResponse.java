package com.project.tesi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO di risposta per una recensione.
 * Mostra solo il nome dell'autore (non il cognome) per motivi di privacy.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    /** Nome dell'autore della recensione (solo il nome, per privacy). */
    private String authorName;

    /** Voto numerico da 1 a 5. */
    private int rating;

    /** Commento testuale (può essere null). */
    private String comment;

    /** Data e ora in cui è stata scritta la recensione. */
    private LocalDateTime date;
}