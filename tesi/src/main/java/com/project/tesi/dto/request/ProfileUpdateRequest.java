package com.project.tesi.dto.request;

import lombok.Data;

/**
 * DTO di richiesta per l'aggiornamento del profilo utente.
 * Tutti i campi sono opzionali: vengono aggiornati solo quelli non null.
 */
@Data
public class ProfileUpdateRequest {

    /** Nuovo nome dell'utente (opzionale). */
    private String firstName;

    /** Nuovo cognome dell'utente (opzionale). */
    private String lastName;

    /** Nuova password in chiaro, verrà hashata prima del salvataggio (opzionale). */
    private String password;

    /** Nuova immagine profilo in formato Base64 (opzionale). */
    private String profilePicture;
}
