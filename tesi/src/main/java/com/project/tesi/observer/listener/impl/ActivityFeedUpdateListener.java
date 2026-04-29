package com.project.tesi.observer.listener.impl;

import com.project.tesi.enums.EventType;
import com.project.tesi.model.Booking;
import com.project.tesi.observer.listener.EventListener;
import com.project.tesi.observer.manager.EventManager;
import com.project.tesi.service.ActivityFeedService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Listener Observer per l'evento {@code BOOKING_CREATED}.
 *
 * <p>Implementa il Design Pattern <b>Observer</b>: si registra all'{@link EventManager}
 * al momento della costruzione ({@code @PostConstruct}) e si deregistra alla distruzione
 * ({@code @PreDestroy}). Quando riceve una notifica, delega al {@link ActivityFeedService}
 * la responsabilità di persistere l'evento nel database, rispettando il principio
 * di separazione dei layer: il listener non contiene logica di business o accesso diretto
 * al repository, ma funge esclusivamente da punto di smistamento degli eventi.</p>
 */
@Component
public class ActivityFeedUpdateListener implements EventListener<Booking> {

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

    /**
     * Riceve la notifica di una nuova prenotazione e delega al service
     * la registrazione dell'attività nel layer di persistenza.
     *
     * @param booking la prenotazione appena creata
     */
    @Override
    public void update(Booking booking) {
        activityFeedService.logBookingCreated(booking);
    }
}
