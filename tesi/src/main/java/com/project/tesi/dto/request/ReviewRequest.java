package com.project.tesi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di richiesta per la creazione di una recensione.
 * Il cliente valuta un professionista con un voto da 1 a 5
 * e un commento testuale opzionale.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long professionalId;

    @Min(1) @Max(5)
    private int rating;

    private String comment;
}