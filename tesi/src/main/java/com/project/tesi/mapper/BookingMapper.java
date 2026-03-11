package com.project.tesi.mapper;

import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Slot;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Mapper per la conversione dell'entità {@link Booking} nel DTO {@link BookingResponse}.
 * Formatta date e orari in stringhe leggibili e determina se l'utente
 * può accedere alla videochiamata.
 */
@Component
public class BookingMapper {

    /** Formato orario per startTime e endTime (es. "09:30"). */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /** Formato data per il campo date (es. "2026-03-11"). */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Converte un'entità Booking nel DTO di risposta.
     * Estrae i dati dallo slot associato e costruisce nomi completi
     * per professionista e cliente.
     *
     * @param booking l'entità prenotazione (può essere null)
     * @return il DTO di risposta, oppure {@code null} se booking è null
     */
    public BookingResponse toResponse(Booking booking) {
        if (booking == null) return null;

        Slot slot = booking.getSlot();
        LocalDateTime start = slot.getStartTime();
        LocalDateTime end = slot.getEndTime();

        return BookingResponse.builder()
                .id(booking.getId())
                .date(start.format(DATE_FORMATTER))
                .startTime(start.format(TIME_FORMATTER))
                .endTime(end.format(TIME_FORMATTER))
                .professionalName(booking.getProfessional().getFirstName() + " " + booking.getProfessional().getLastName())
                .clientName(booking.getUser().getFirstName() + " " + booking.getUser().getLastName())
                .professionalRole(booking.getProfessional().getRole())
                .meetingLink(booking.getMeetingLink())
                .status(booking.getStatus())
                .canJoin(isMeetingJoinable(start))
                .build();
    }

    /**
     * Determina se la videochiamata è accessibile.
     * Attualmente restituisce sempre {@code true} (da implementare con finestra temporale).
     *
     * @param startTime orario di inizio dell'appuntamento
     * @return {@code true} se l'utente può accedere alla videochiamata
     */
    private boolean isMeetingJoinable(LocalDateTime startTime) {
        return true;
    }
}
