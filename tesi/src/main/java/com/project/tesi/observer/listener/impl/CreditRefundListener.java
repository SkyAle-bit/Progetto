package com.project.tesi.observer.listener.impl;

import com.project.tesi.enums.EventType;
import com.project.tesi.model.Booking;
import com.project.tesi.observer.listener.EventListener;
import com.project.tesi.observer.manager.EventManager;
import com.project.tesi.service.SubscriptionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Listener Observer per il rimborso dei crediti alla cancellazione di una prenotazione.
 *
 * <p>Implementa il Design Pattern <b>Observer</b>: si registra all'{@link EventManager}
 * per l'evento {@code BOOKING_CANCELLED} e, alla ricezione della notifica, delega al
 * {@link SubscriptionService} la logica di rimborso dei crediti. In questo modo si rispetta
 * rigorosamente il principio di separazione dei layer: il listener non accede direttamente
 * al {@code SubscriptionRepository} (che sarebbe una violazione architetturale),
 * ma invoca il metodo di servizio che incapsula la strategia corretta.</p>
 */
@Component
public class CreditRefundListener implements EventListener<Booking> {

    private final EventManager eventManager;
    private final SubscriptionService subscriptionService;

    // Costruttore esplicito — sostituisce @RequiredArgsConstructor di Lombok
    public CreditRefundListener(EventManager eventManager,
                                SubscriptionService subscriptionService) {
        this.eventManager = eventManager;
        this.subscriptionService = subscriptionService;
    }

    @PostConstruct
    public void init() {
        eventManager.subscribe(EventType.BOOKING_CANCELLED, this);
    }

    @PreDestroy
    public void destroy() {
        eventManager.unsubscribe(EventType.BOOKING_CANCELLED, this);
    }

    /**
     * Riceve la notifica di una prenotazione annullata e delega al service
     * il rimborso dei crediti sull'abbonamento dell'utente.
     *
     * @param booking la prenotazione annullata
     */
    @Override
    public void update(Booking booking) {
        subscriptionService.refundCredits(booking);
    }
}
