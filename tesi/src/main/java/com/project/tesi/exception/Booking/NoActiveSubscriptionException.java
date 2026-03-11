package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando non esiste un abbonamento attivo per l'utente (422 Unprocessable Entity).
 */
public class NoActiveSubscriptionException extends BaseException {

    public NoActiveSubscriptionException() {
        super("Nessun abbonamento attivo trovato. Attiva un piano per prenotare.", HttpStatus.valueOf(422));
    }

    public NoActiveSubscriptionException(String message) {
        super(message, HttpStatus.valueOf(422));
    }
}


