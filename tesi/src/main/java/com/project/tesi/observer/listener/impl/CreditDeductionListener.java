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
public class CreditDeductionListener implements EventListener<Booking> {

    private final EventManager eventManager;
    private final SubscriptionRepository subscriptionRepository;
    private final List<BookingStrategy> strategies;

    // Costruttore esplicito — sostituisce @RequiredArgsConstructor di Lombok
    public CreditDeductionListener(EventManager eventManager,
                                   SubscriptionRepository subscriptionRepository,
                                   List<BookingStrategy> strategies) {
        this.eventManager = eventManager;
        this.subscriptionRepository = subscriptionRepository;
        this.strategies = strategies;
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
        User user = booking.getUser();
        User professional = booking.getProfessional();

        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new IllegalStateException(
                        "Abbonamento non trovato per l'utente " + user.getId()));

        // Stream più leggibile e idiomatico rispetto al loop manuale
        BookingStrategy strategy = strategies.stream()
                .filter(s -> s.getSupportedRole() == professional.getRole())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Nessuna strategy trovata per il ruolo: " + professional.getRole()));

        strategy.consumeCredits(sub);
        subscriptionRepository.save(sub);
    }
}
