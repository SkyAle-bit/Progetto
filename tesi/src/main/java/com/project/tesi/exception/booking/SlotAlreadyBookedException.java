package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando uno slot è già stato prenotato da un altro utente
 * o non è più disponibile per Optimistic Locking (409 Conflict).
 */
public class SlotAlreadyBookedException extends BaseException {

    /** Costruttore con messaggio predefinito. */
    public SlotAlreadyBookedException() {
        super("Lo slot selezionato non è più disponibile.", HttpStatus.CONFLICT);
    }

    /** @param message messaggio personalizzato */
    public SlotAlreadyBookedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}