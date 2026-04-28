package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando i crediti dell'abbonamento per un tipo di professionista
 * sono esauriti (422 Unprocessable Entity).
 * Es: il cliente ha usato tutti i crediti PT del mese.
 */
public class InsufficientCreditsException extends BaseException {

    /** @param professionalType tipo di credito esaurito (es. "PT", "Nutrizionista") */
    public InsufficientCreditsException(String professionalType) {
        super("Crediti " + professionalType + " esauriti. Aggiorna il tuo abbonamento per continuare.", HttpStatus.valueOf(422));
    }
}


