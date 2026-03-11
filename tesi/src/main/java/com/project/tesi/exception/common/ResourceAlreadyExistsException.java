package com.project.tesi.exception.common;

import org.springframework.http.HttpStatus;

/**
 * Eccezione per risorse già esistenti (409 Conflict).
 * Usata quando si tenta di creare un'entità che viola un vincolo di unicità.
 */
public class ResourceAlreadyExistsException extends BaseException {

    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    public ResourceAlreadyExistsException(String resourceName, String fieldName, String fieldValue) {
        super(resourceName + " con " + fieldName + " '" + fieldValue + "' esiste già.", HttpStatus.CONFLICT);
    }
}

