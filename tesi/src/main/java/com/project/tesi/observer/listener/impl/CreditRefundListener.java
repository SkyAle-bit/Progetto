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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreditRefundListener implements EventListener<Booking> {

    private final EventManager eventManager;
    private final SubscriptionRepository subscriptionRepository;
    private final List<BookingStrategy> strategies;

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
        BookingStrategy strategy = null;
        for (BookingStrategy s : strategies) {
            if (s.getSupportedRole() == professional.getRole()) {
                strategy = s;
                break;
            }
        }

        if (strategy != null) {
            Subscription sub = subscriptionRepository.findByUserAndActiveTrue(booking.getUser())
                    .orElse(null);
            if (sub != null) {
                strategy.refundCredits(sub);
                subscriptionRepository.save(sub);
            }
        }
    }
}
