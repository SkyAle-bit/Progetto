package com.project.tesi.exception.auth;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione per credenziali di login non valide (401 Unauthorized).
 * Lanciata quando email o password non corrispondono a un account esistente.
 */
public class InvalidCredentialsException extends BaseException {

    /** Costruttore con messaggio predefinito. */
    public InvalidCredentialsException() {
        super("Email o password non validi.", HttpStatus.UNAUTHORIZED);
    }

    /** @param message messaggio personalizzato */
    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

