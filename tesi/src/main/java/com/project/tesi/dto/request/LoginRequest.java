package com.project.tesi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenziali richieste per effettuare l'accesso e ottenere il token JWT")
public class LoginRequest {

    @Schema(description = "L'indirizzo email dell'utente", example = "pt@test.com")
    @NotBlank(message = "L'email non può essere vuota")
    private String email;

    @Schema(description = "La password dell'utente", example = "password")
    @NotBlank(message = "La password non può essere vuota")
    private String password;
}