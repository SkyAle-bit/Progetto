package com.project.tesi.observer.listener.impl;

import com.project.tesi.enums.EventType;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.observer.listener.EventListener;
import com.project.tesi.observer.manager.EventManager;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.service.strategy.BookingStrategy;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreditRefundListener implements EventListener<Booking> {

    private final EventManager eventManager;
    private final SubscriptionRepository subscriptionRepository;
    private final List<BookingStrategy> strategies;

    // Costruttore esplicito — sostituisce @RequiredArgsConstructor di Lombok
    public CreditRefundListener(EventManager eventManager, SubscriptionRepository subscriptionRepository, List<BookingStrategy> strategies) {
        this.eventManager = eventManager;
        this.subscriptionRepository = subscriptionRepository;
        this.strategies = strategies;
    }

    @PostConstruct
    public void init() {
        eventManager.subscribe(EventType.BOOKING_CANCELLED, this);
    }

    @PreDestroy
    public void destroy() {
        eventManager.unsubscribe(EventType.BOOKING_CANCELLED, this);
    }

    @Override
    public void update(Booking booking) {
        User professional = booking.getProfessional();
        
        BookingStrategy strategy = strategies.stream()
                .filter(s -> s.getSupportedRole() == professional.getRole())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nessuna strategy trovata per il ruolo: " + professional.getRole()));

        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(booking.getUser())
                .orElseThrow(() -> new IllegalStateException("Nessun abbonamento attivo trovato per l'utente"));

        strategy.refundCredits(sub);
        subscriptionRepository.save(sub);
    }
}
