package com.project.tesi.observer.listener.impl;

import com.project.tesi.event.BookingCreatedEvent;
import com.project.tesi.model.Booking;
import com.project.tesi.model.User;
import com.project.tesi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener per l'evento BookingCreatedEvent.
 * Attivato solo dopo il commit della transazione per garantire che l'email
 * parta esclusivamente quando la prenotazione è stata persista con successo.
 */
@Component
@RequiredArgsConstructor
public class BookingEmailNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(BookingEmailNotificationListener.class);

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("emailTaskExecutor")
    public void handleBookingCreated(BookingCreatedEvent event) {
        Booking booking = event.getBooking();
        try {
            User client = booking.getUser();
            User professional = booking.getProfessional();

            emailService.sendBookingConfirmationEmail(
                    client.getEmail(),
                    client.getFirstName(),
                    professional.getFirstName() + " " + professional.getLastName(),
                    booking.getSlot().getStartTime(),
                    booking.getMeetingLink()
            );

            emailService.sendBookingConfirmationEmail(
                    professional.getEmail(),
                    professional.getFirstName(),
                    client.getFirstName() + " " + client.getLastName(),
                    booking.getSlot().getStartTime(),
                    booking.getMeetingLink()
            );
        } catch (Exception e) {
            log.error("Invio email conferma fallito per booking #{}: {}", booking.getId(), e.getMessage());
        }
    }
}
