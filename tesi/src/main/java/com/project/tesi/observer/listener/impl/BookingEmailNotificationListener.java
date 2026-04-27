package com.project.tesi.observer.listener.impl;

import com.project.tesi.enums.EventType;
import com.project.tesi.model.Booking;
import com.project.tesi.model.User;
import com.project.tesi.observer.listener.EventListener;
import com.project.tesi.observer.manager.EventManager;
import com.project.tesi.service.EmailService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Listener responsabile dell'invio delle email di conferma prenotazione.
 * Reagisce all'evento BOOKING_CREATED.
 */
@Component
@RequiredArgsConstructor
public class BookingEmailNotificationListener implements EventListener<Booking> {

    private final EventManager eventManager;
    private final EmailService emailService;

    @PostConstruct
    public void init() {
        eventManager.subscribe(EventType.BOOKING_CREATED, this);
    }

    @Override
    public void update(Booking booking) {
        User client = booking.getUser();
        User professional = booking.getProfessional();

        // Invia email al cliente
        emailService.sendBookingConfirmationEmail(
                client.getEmail(),
                client.getFirstName(),
                professional.getFirstName() + " " + professional.getLastName(),
                booking.getSlot().getStartTime(),
                booking.getMeetingLink()
        );

        // Invia email al professionista (opzionale ma coerente)
        emailService.sendBookingConfirmationEmail(
                professional.getEmail(),
                professional.getFirstName(),
                client.getFirstName() + " " + client.getLastName(),
                booking.getSlot().getStartTime(),
                booking.getMeetingLink()
        );
    }
}
