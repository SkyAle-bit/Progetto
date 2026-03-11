package com.project.tesi.exception.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Classe base per tutte le eccezioni custom dell'applicazione.
 * Permette di associare un HttpStatus a ogni eccezione.
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final HttpStatus status;

    protected BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    protected BaseException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}

