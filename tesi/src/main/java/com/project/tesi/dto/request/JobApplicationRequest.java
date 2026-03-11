package com.project.tesi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO di richiesta per l'invio di una candidatura lavorativa.
 * I campi sono validati con Bean Validation (JSR 380).
 * Il CV viene inviato come file separato (MultipartFile).
 */
@Data
public class JobApplicationRequest {

    /** Nome del candidato. */
    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    /** Cognome del candidato. */
    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    /** Email di contatto del candidato. */
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    private String email;

    /** Ruolo per cui ci si candida: "PERSONAL_TRAINER" o "NUTRITIONIST". */
    @NotBlank(message = "Il ruolo è obbligatorio")
    private String role;

    /** Messaggio motivazionale allegato alla candidatura. */
    @NotBlank(message = "Il messaggio motivazionale è obbligatorio")
    private String message;
}
