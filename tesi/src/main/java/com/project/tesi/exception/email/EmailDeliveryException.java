package com.project.tesi.exception.email;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando l'invio email verso provider esterno fallisce.
 */
public class EmailDeliveryException extends BaseException {

    public EmailDeliveryException(String message) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public EmailDeliveryException(String message, String providerBody) {
        super(providerBody == null || providerBody.isBlank() ? message : message + " - " + providerBody,
                HttpStatus.SERVICE_UNAVAILABLE);
    }

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, HttpStatus.SERVICE_UNAVAILABLE, cause);
    }
}



