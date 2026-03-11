package com.project.tesi.exception.common;

import org.springframework.http.HttpStatus;

/**
 * Eccezione per accesso non autorizzato a una risorsa o operazione (403 Forbidden).
 * Usata quando un utente autenticato tenta un'operazione per cui non ha i permessi
 * in base al proprio ruolo.
 */
public class UnauthorizedAccessException extends BaseException {

    /** @param message descrizione del permesso mancante */

    public UnauthorizedAccessException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}

