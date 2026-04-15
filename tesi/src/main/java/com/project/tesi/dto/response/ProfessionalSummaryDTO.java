package com.project.tesi.dto.response;

import com.project.tesi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta con il riepilogo di un professionista.
 * Usato nella vetrina pubblica e nella dashboard del cliente
 * per mostrare i professionisti disponibili o assegnati.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalSummaryDTO {

    private Long id;

    private String fullName;

    private Double averageRating;

    private Integer currentActiveClients;

    private boolean isSoldOut;

    private Role role;
}