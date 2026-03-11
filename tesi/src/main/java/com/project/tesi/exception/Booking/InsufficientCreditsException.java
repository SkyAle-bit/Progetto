package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando i crediti dell'abbonamento sono esauriti (422 Unprocessable Entity).
 */
public class InsufficientCreditsException extends BaseException {

    public InsufficientCreditsException(String professionalType) {
        super("Crediti " + professionalType + " esauriti. Aggiorna il tuo abbonamento per continuare.", HttpStatus.valueOf(422));
    }
}


