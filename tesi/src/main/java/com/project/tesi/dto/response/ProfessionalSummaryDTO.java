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

    /** ID del professionista. */
    private Long id;

    /** Nome completo (Nome + Cognome concatenati). */
    private String fullName;

    /** Media voti ricevuti dalle recensioni (1.0 – 5.0, null se nessuna recensione). */
    private Double averageRating;

    /** Numero attuale di clienti assegnati (es. 7 su un massimo di 10). */
    private Integer currentActiveClients;

    /** Indica se il professionista ha raggiunto il limite massimo di clienti. */
    private boolean isSoldOut;

    /** Ruolo del professionista (PERSONAL_TRAINER o NUTRITIONIST). */
    private Role role;
}