package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando un cliente tenta una prenotazione senza avere
 * un abbonamento attivo (422 Unprocessable Entity).
 */
public class NoActiveSubscriptionException extends BaseException {

    /** Costruttore con messaggio predefinito. */
    public NoActiveSubscriptionException() {
        super("Nessun abbonamento attivo trovato. Attiva un piano per prenotare.", HttpStatus.valueOf(422));
    }

    /** @param message messaggio personalizzato */
    public NoActiveSubscriptionException(String message) {
        super(message, HttpStatus.valueOf(422));
    }
}


