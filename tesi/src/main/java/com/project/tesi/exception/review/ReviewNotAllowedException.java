package com.project.tesi.exception.review;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando un cliente non può ancora recensire un professionista (422 Unprocessable Entity).
 * Motivi possibili: registrazione troppo recente (meno di 1 mese) oppure recensione già lasciata.
 */
public class ReviewNotAllowedException extends BaseException {

    /** @param message descrizione del motivo per cui la recensione è bloccata */

    public ReviewNotAllowedException(String message) {
        super(message, HttpStatus.valueOf(422));
    }
}


