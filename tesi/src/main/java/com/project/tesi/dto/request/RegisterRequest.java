package com.project.tesi.dto.request;

import com.project.tesi.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    @Email(message = "Email non valida")
    @NotBlank(message = "L'email è obbligatoria")
    private String email;

    @NotBlank
    @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
    private String password;

    @NotNull(message = "Il ruolo è obbligatorio")
    private Role role; // CLIENT, PERSONAL_TRAINER, NUTRITIONIST

    // Opzionali: usati solo se l'utente è un CLIENT che deve scegliere i professionisti
    private Long selectedPtId;
    private Long selectedNutritionistId;
}