package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando uno slot è già prenotato o non più disponibile (409 Conflict).
 */
public class SlotAlreadyBookedException extends BaseException {

    public SlotAlreadyBookedException() {
        super("Lo slot selezionato non è più disponibile.", HttpStatus.CONFLICT);
    }

    public SlotAlreadyBookedException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}