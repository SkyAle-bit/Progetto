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

    private String authorName;

    private int rating;

    private String comment;

    private LocalDateTime date;
}