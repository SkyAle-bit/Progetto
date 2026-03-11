package com.project.tesi.exception.subscription;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando non viene trovato un abbonamento attivo per l'utente (404 Not Found).
 */
public class SubscriptionNotFoundException extends BaseException {

    public SubscriptionNotFoundException() {
        super("Nessun abbonamento attivo trovato.", HttpStatus.NOT_FOUND);
    }

    public SubscriptionNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

