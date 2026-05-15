package com.project.tesi.observer.listener.impl;

import com.project.tesi.event.BookingCreatedEvent;
import com.project.tesi.service.ActivityFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener che traccia la creazione di una prenotazione nel feed attività.
 * Attivato dopo il commit per garantire che il log sia scritto solo per
 * prenotazioni effettivamente salvate nel database.
 */
@Component
@RequiredArgsConstructor
public class ActivityFeedUpdateListener {

    private final ActivityFeedService activityFeedService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookingCreated(BookingCreatedEvent event) {
        activityFeedService.logBookingCreated(event.getBooking());
    }
}
