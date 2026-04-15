package com.project.tesi.dto.request;

import lombok.Data;

/**
 * DTO di richiesta per l'aggiornamento del profilo utente.
 * Tutti i campi sono opzionali: vengono aggiornati solo quelli non null.
 */
@Data
public class ProfileUpdateRequest {

    private String firstName;

    private String lastName;

    private String password;

    private String profilePicture;
}
