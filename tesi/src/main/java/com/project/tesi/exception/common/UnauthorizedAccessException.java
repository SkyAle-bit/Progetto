package com.project.tesi.exception.common;

import org.springframework.http.HttpStatus;

/**
 * Eccezione per accesso non autorizzato (403 Forbidden).
 * Usata quando un utente tenta un'operazione per cui non ha i permessi.
 */
public class UnauthorizedAccessException extends BaseException {

    public UnauthorizedAccessException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}

