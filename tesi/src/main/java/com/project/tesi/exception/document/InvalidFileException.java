package com.project.tesi.exception.document;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando un file caricato non è valido (400 Bad Request).
 * Es: estensione mancante, formato non supportato, tipo documento non coerente con il ruolo.
 */
public class InvalidFileException extends BaseException {

    public InvalidFileException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

