package com.project.tesi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di richiesta per il login.
 * Contiene le credenziali (email + password) necessarie per l'autenticazione
 * e il rilascio del token JWT.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credenziali richieste per effettuare l'accesso e ottenere il token JWT")
public class LoginRequest {

    /** Indirizzo email dell'utente (usato come username). */
    @Schema(description = "L'indirizzo email dell'utente", example = "pt@test.com")
    @NotBlank(message = "L'email non può essere vuota")
    private String email;

    /** Password dell'utente. */
    @Schema(description = "La password dell'utente", example = "password")
    @NotBlank(message = "La password non può essere vuota")
    private String password;
}