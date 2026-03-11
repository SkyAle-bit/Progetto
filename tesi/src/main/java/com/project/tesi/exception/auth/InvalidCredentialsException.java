package com.project.tesi.exception.auth;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione per credenziali di login non valide (401 Unauthorized).
 */
public class InvalidCredentialsException extends BaseException {

    public InvalidCredentialsException() {
        super("Email o password non validi.", HttpStatus.UNAUTHORIZED);
    }

    public InvalidCredentialsException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

