package com.project.tesi.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull
    private Long userId; // Autore

    @NotNull
    private Long professionalId; // Destinatario

    @Min(1) @Max(5)
    private int rating;

    private String comment;
}