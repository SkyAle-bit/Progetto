package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando il cliente non è assegnato al professionista richiesto (403 Forbidden).
 */
public class ProfessionalNotAssignedException extends BaseException {

    public ProfessionalNotAssignedException(String professionalType) {
        super("Non sei assegnato a questo " + professionalType + ".", HttpStatus.FORBIDDEN);
    }

    public ProfessionalNotAssignedException(String professionalType, String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}

