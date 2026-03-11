package com.project.tesi.exception.common;

import org.springframework.http.HttpStatus;

/**
 * Eccezione per operazioni non consentite a causa di regole di business (422 Unprocessable Entity).
 * Usata quando la richiesta è valida sintatticamente ma viola una regola di dominio.
 */
public class BusinessLogicException extends BaseException {

    public BusinessLogicException(String message) {
        super(message, HttpStatus.valueOf(422));
    }
}


