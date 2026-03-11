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

    /** Token JWT da includere nell'header Authorization delle richieste successive. */
    private String token;

    /** Tipo di token (sempre "Bearer"). */
    @Builder.Default
    private String type = "Bearer";

    /** ID dell'utente autenticato. */
    private Long id;

    /** Nome dell'utente. */
    private String firstName;

    /** Cognome dell'utente. */
    private String lastName;

    /** Email dell'utente. */
    private String email;

    /** Ruolo dell'utente (determina la navigazione nel frontend). */
    private Role role;

    /** Immagine profilo in Base64 (per la visualizzazione immediata nell'header). */
    private String profilePicture;
}