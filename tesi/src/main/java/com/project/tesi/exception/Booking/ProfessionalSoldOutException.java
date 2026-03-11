package com.project.tesi.exception.booking;

import com.project.tesi.exception.common.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Eccezione lanciata quando un professionista ha raggiunto il limite massimo di clienti (422 Unprocessable Entity).
 */
public class ProfessionalSoldOutException extends BaseException {

    public ProfessionalSoldOutException(String professionalName) {
        super("Il professionista " + professionalName + " è attualmente Sold Out.", HttpStatus.valueOf(422));
    }
}


