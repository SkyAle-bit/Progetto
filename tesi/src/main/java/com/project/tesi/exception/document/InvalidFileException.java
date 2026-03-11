package com.project.tesi.exception.document;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando un file caricato non è valido (400 Bad Request).
 * Esempi: estensione mancante, formato non supportato, tipo documento
 * non coerente con il ruolo dell'uploader (es. un PT che carica un piano alimentare).
 */
public class InvalidFileException extends BaseException {

    /** @param message descrizione del problema con il file */

    public InvalidFileException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

