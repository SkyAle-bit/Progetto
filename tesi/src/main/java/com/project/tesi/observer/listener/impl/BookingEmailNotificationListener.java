package com.project.tesi.observer.listener.impl;

import com.project.tesi.enums.EventType;
import com.project.tesi.model.Booking;
import com.project.tesi.model.User;
import com.project.tesi.observer.listener.EventListener;
import com.project.tesi.observer.manager.EventManager;
import com.project.tesi.service.EmailService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class BookingEmailNotificationListener implements EventListener<Booking> {

    private final EventManager eventManager;
    private final EmailService emailService;

    // Costruttore esplicito — sostituisce @RequiredArgsConstructor di Lombok
    public BookingEmailNotificationListener(EventManager eventManager, EmailService emailService) {
        this.eventManager = eventManager;
        this.emailService = emailService;
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
        User client = booking.getUser();
        User professional = booking.getProfessional();

        emailService.sendBookingConfirmationEmail(
                client.getEmail(),
                client.getFirstName(),
                professional.getFirstName() + " " + professional.getLastName(),
                booking.getSlot().getStartTime(),
                booking.getMeetingLink()
        );

        emailService.sendBookingConfirmationEmail(
                professional.getEmail(),
                professional.getFirstName(),
                client.getFirstName() + " " + client.getLastName(),
                booking.getSlot().getStartTime(),
                booking.getMeetingLink()
        );
    }
}
