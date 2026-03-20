package com.project.tesi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO per il reset della password tramite token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Il token è obbligatorio")
    private String token;

    @NotBlank(message = "La nuova password è obbligatoria")
    @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
    private String newPassword;
}
