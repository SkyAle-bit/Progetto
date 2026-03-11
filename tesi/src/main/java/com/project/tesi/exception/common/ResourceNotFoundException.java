package com.project.tesi.exception.common;

import org.springframework.http.HttpStatus;

/**
 * Eccezione generica per risorse non trovate (404).
 * Usata quando un'entità richiesta non esiste nel database.
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " con ID " + id + " non trovato.", HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(resourceName + " con " + fieldName + " '" + fieldValue + "' non trovato.", HttpStatus.NOT_FOUND);
    }
}

