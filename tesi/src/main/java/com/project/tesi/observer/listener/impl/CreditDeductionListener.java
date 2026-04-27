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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener responsabile della scalatura dei crediti dall'abbonamento dell'utente.
 * Reagisce all'evento BOOKING_CREATED.
 */
@Component
@RequiredArgsConstructor
public class CreditDeductionListener implements EventListener<Booking> {

    private final EventManager eventManager;
    private final SubscriptionRepository subscriptionRepository;
    private final List<BookingStrategy> strategies;

    @PostConstruct
    public void init() {
        eventManager.subscribe(EventType.BOOKING_CREATED, this);
    }

    @Override
    public void update(Booking booking) {
        User user = booking.getUser();
        User professional = booking.getProfessional();

        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new IllegalStateException("Abbonamento non trovato per l'utente " + user.getId()));

        BookingStrategy strategy = null;
        for (BookingStrategy s : strategies) {
            if (s.getSupportedRole() == professional.getRole()) {
                strategy = s;
                break;
            }
        }
        
        if (strategy == null) {
            throw new IllegalStateException("Il professionista non è né PT né Nutrizionista");
        }

        // Scala il credito e salva
        strategy.consumeCredits(sub);
        subscriptionRepository.save(sub);
    }
}
