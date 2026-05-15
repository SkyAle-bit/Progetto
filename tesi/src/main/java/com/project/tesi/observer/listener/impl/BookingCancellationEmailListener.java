package com.project.tesi.observer.listener.impl;

import com.project.tesi.event.BookingCancelledEvent;
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
 * Listener per l'evento BookingCancelledEvent.
 * Attivato solo dopo il commit per evitare email di cancellazione spurie
 * in caso di rollback della transazione.
 */
@Component
@RequiredArgsConstructor
public class BookingCancellationEmailListener {

    private static final Logger log = LoggerFactory.getLogger(BookingCancellationEmailListener.class);

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("emailTaskExecutor")
    public void handleBookingCancelled(BookingCancelledEvent event) {
        Booking booking = event.getBooking();
        try {
            User client = booking.getUser();
            User professional = booking.getProfessional();

            emailService.sendBookingCancellationEmail(
                    client.getEmail(),
                    client.getFirstName(),
                    professional.getFirstName() + " " + professional.getLastName(),
                    booking.getSlot().getStartTime()
            );

            emailService.sendBookingCancellationEmail(
                    professional.getEmail(),
                    professional.getFirstName(),
                    client.getFirstName() + " " + client.getLastName(),
                    booking.getSlot().getStartTime()
            );
        } catch (Exception e) {
            log.error("Invio email cancellazione fallito per booking #{}: {}", booking.getId(), e.getMessage());
        }
    }
}
