package com.project.tesi.observer.listener.impl;

import com.project.tesi.enums.EventType;
import com.project.tesi.model.Booking;
import com.project.tesi.observer.listener.Observer;
import com.project.tesi.observer.manager.EventManager;
import com.project.tesi.service.ActivityFeedService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Listener che si mette in ascolto dell'evento BOOKING_CREATED.
 * Appena qualcuno prenota, intercetta l'evento e dice ad ActivityFeedService di loggare 
 * l'attività nel database. In questo modo disaccoppiamo la logica di tracciamento.
 */
@Component
public class ActivityFeedUpdateListener implements Observer<Booking> {

    private final EventManager eventManager;
    private final ActivityFeedService activityFeedService;

    // Costruttore esplicito — sostituisce @RequiredArgsConstructor di Lombok
    public ActivityFeedUpdateListener(EventManager eventManager,
                                      ActivityFeedService activityFeedService) {
        this.eventManager = eventManager;
        this.activityFeedService = activityFeedService;
    }

    @PostConstruct
    public void init() {
        eventManager.subscribe(EventType.BOOKING_CREATED, this);
    }

    @PreDestroy
    public void destroy() {
        eventManager.unsubscribe(EventType.BOOKING_CREATED, this);
    }

    // Metodo triggerato dall'EventManager. Passiamo la palla al service.
    @Override
    public void update(Booking booking) {
        activityFeedService.logBookingCreated(booking);
    }
}
