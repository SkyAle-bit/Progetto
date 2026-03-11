package com.project.tesi.exception.common;

import org.springframework.http.HttpStatus;

/**
 * Eccezione per risorse già esistenti (409 Conflict).
 * Usata quando si tenta di creare un'entità che viola un vincolo di unicità
 * (es. email già registrata, piano con nome duplicato).
 */
public class ResourceAlreadyExistsException extends BaseException {

    /** @param message messaggio personalizzato */
    public ResourceAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }

    /**
     * Costruttore con nome risorsa, campo e valore per messaggi auto-generati.
     * @param resourceName tipo di risorsa (es. "Utente")
     * @param fieldName    campo che viola l'unicità (es. "email")
     * @param fieldValue   valore duplicato
     */
    public ResourceAlreadyExistsException(String resourceName, String fieldName, String fieldValue) {
        super(resourceName + " con " + fieldName + " '" + fieldValue + "' esiste già.", HttpStatus.CONFLICT);
    }
}

