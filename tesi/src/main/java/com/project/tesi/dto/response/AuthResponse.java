package com.project.tesi.dto.response;

import com.project.tesi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta restituito dopo un login riuscito.
 * Contiene il token JWT e i dati principali dell'utente autenticato,
 * necessari al frontend per inizializzare la sessione.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;

    private String profilePicture;
}