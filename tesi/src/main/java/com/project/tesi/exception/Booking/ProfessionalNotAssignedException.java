package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando un cliente tenta di prenotare con un professionista
 * a cui non è assegnato (403 Forbidden).
 */
public class ProfessionalNotAssignedException extends BaseException {

    /** @param professionalType tipo di professionista (es. "PT", "Nutrizionista") */
    public ProfessionalNotAssignedException(String professionalType) {
        super("Non sei assegnato a questo " + professionalType + ".", HttpStatus.FORBIDDEN);
    }

    /**
     * @param professionalType tipo di professionista (ignorato, presente per compatibilità)
     * @param message          messaggio personalizzato
     */
    public ProfessionalNotAssignedException(String professionalType, String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}

