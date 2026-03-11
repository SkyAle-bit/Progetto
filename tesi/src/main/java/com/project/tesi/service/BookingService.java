package com.project.tesi.service;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;

/**
 * Interfaccia del servizio per la gestione delle prenotazioni.
 * Verifica crediti, assegnazione e disponibilità dello slot prima di creare la prenotazione.
 */
public interface BookingService {

    /** Crea una nuova prenotazione verificando tutti i vincoli di business. */
    BookingResponse createBooking(BookingRequest request);
}