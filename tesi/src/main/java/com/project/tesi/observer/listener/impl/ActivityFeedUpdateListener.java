package com.project.tesi.observer.listener.impl;

import com.project.tesi.enums.EventType;
import com.project.tesi.model.Booking;
import com.project.tesi.observer.listener.EventListener;
import com.project.tesi.observer.manager.EventManager;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ActivityFeedUpdateListener implements EventListener<Booking> {

    private static final Logger log = LoggerFactory.getLogger(ActivityFeedUpdateListener.class);

    private final EventManager eventManager;

    // Costruttore esplicito — sostituisce @RequiredArgsConstructor di Lombok
    public ActivityFeedUpdateListener(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @PostConstruct
    public void init() {
        eventManager.subscribe(EventType.BOOKING_CREATED, this);
    }

    @PreDestroy
    public void destroy() {
        eventManager.unsubscribe(EventType.BOOKING_CREATED, this);
    }

    @Override
    public void update(Booking booking) {
        log.info("ActivityFeed: Nuova prenotazione creata! Utente {} con Professionista {} il {}",
                booking.getUser().getEmail(),
                booking.getProfessional().getEmail(),
                booking.getSlot().getStartTime());
    }
}
