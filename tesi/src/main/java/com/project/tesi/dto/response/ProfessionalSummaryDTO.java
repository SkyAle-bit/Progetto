package com.project.tesi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalSummaryDTO {
    private Long id;
    private String fullName; // Nome + Cognome concatenati
    private Double averageRating;
    private Integer currentActiveClients; // Per mostrare es: 42/50
    private boolean isSoldOut; // Se ha raggiunto i 50 utenti
}