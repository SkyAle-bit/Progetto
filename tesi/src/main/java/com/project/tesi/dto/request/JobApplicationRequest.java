package com.project.tesi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

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
    private String role; // "PERSONAL_TRAINER" o "NUTRITIONIST"

    @NotBlank(message = "Il messaggio motivazionale è obbligatorio")
    private String message;
}
