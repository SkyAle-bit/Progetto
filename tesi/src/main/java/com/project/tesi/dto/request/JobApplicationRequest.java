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

    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Formato email non valido")
    private String email;

    @NotBlank(message = "Il ruolo è obbligatorio")
    private String role;

    @NotBlank(message = "Il messaggio motivazionale è obbligatorio")
    private String message;
}
