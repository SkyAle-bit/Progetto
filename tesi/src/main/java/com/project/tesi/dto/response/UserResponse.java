package com.project.tesi.dto.response;

import com.project.tesi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta con il profilo completo di un utente.
 * Include dati anagrafici, ruolo e informazioni aggiuntive
 * a seconda del tipo di utente (cliente o professionista).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /** ID dell'utente. */
    private Long id;

    /** Nome dell'utente. */
    private String firstName;

    /** Cognome dell'utente. */
    private String lastName;

    /** Email dell'utente. */
    private String email;

    /** Ruolo dell'utente nel sistema. */
    private Role role;

    // ── CAMPI PER I CLIENTI ─────────────────────────────────────

    /** Nome del Personal Trainer assegnato (solo per CLIENT). */
    private String assignedPtName;

    /** Nome del Nutrizionista assegnato (solo per CLIENT). */
    private String assignedNutritionistName;

    // ── CAMPI PER I PROFESSIONISTI ──────────────────────────────

    /** Media voti ricevuti (solo per PT e NUTRITIONIST). */
    private Double averageRating;

    /** Numero di clienti attualmente assegnati (solo per PT e NUTRITIONIST). */
    private Integer activeClientsCount;
}