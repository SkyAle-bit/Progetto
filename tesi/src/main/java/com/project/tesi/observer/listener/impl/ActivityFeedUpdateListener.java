package com.project.tesi.observer.listener.impl;

import com.project.tesi.enums.EventType;
import com.project.tesi.model.Booking;
import com.project.tesi.observer.listener.EventListener;
import com.project.tesi.observer.manager.EventManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Listener responsabile dell'aggiornamento dell'activity feed (tramite log/store).
 * Reagisce all'evento BOOKING_CREATED.
 */
@Component
@RequiredArgsConstructor
public class ActivityFeedUpdateListener implements EventListener<Booking> {

    private static final Logger log = LoggerFactory.getLogger(ActivityFeedUpdateListener.class);
    private final EventManager eventManager;

    @PostConstruct
    public void init() {
        eventManager.subscribe(EventType.BOOKING_CREATED, this);
    }

    @Override
    public void update(Booking booking) {
        // Poiché il feed nel progetto è attualmente generato in modo dinamico
        // interrogando le prenotazioni sul DB, qui logghiamo l'evento
        // e manteniamo il disaccoppiamento per future implementazioni con entità Activity dedicata.
        log.info("ActivityFeed: Nuova prenotazione creata! Utente {} con Professionista {} il {}",
                booking.getUser().getEmail(),
                booking.getProfessional().getEmail(),
                booking.getSlot().getStartTime());
    }
}
