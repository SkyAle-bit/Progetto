package com.project.tesi.exception.common;

import org.springframework.http.HttpStatus;

/**
 * Eccezione generica per violazioni di regole di business (422 Unprocessable Entity).
 * Usata quando la richiesta è sintatticamente valida ma viola una regola di dominio
 * non coperta da eccezioni più specifiche.
 */
public class BusinessLogicException extends BaseException {

    /** @param message descrizione della regola di business violata */

    public BusinessLogicException(String message) {
        super(message, HttpStatus.valueOf(422));
    }
}


