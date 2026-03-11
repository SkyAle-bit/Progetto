package com.project.tesi.exception.document;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata per errori di I/O durante il caricamento o il download
 * di documenti dal filesystem (500 Internal Server Error).
 */
public class DocumentStorageException extends BaseException {

    /** @param message descrizione dell'errore I/O */
    public DocumentStorageException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * @param message descrizione dell'errore I/O
     * @param cause   eccezione originale (es. IOException)
     */
    public DocumentStorageException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, cause);
    }
}

