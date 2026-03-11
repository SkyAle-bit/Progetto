package com.project.tesi.exception.chat;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando un utente tenta di chattare con qualcuno
 * senza averne il permesso (403 Forbidden).
 * La chat è consentita solo tra un cliente e un professionista a lui assegnato,
 * oppure con l'Admin per il supporto.
 */
public class ChatNotAllowedException extends BaseException {

    /** Costruttore con messaggio predefinito. */
    public ChatNotAllowedException() {
        super("La chat è permessa solo tra un cliente e un professionista a lui assegnato.", HttpStatus.FORBIDDEN);
    }

    /** @param message messaggio personalizzato */
    public ChatNotAllowedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}

