package com.project.tesi.exception.document;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando un documento richiesto non esiste nel database (404 Not Found).
 */
public class DocumentNotFoundException extends BaseException {

    /** @param documentId ID del documento non trovato */
    public DocumentNotFoundException(Long documentId) {
        super("Documento con ID " + documentId + " non trovato.", HttpStatus.NOT_FOUND);
    }

    /** @param message messaggio personalizzato */
    public DocumentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

