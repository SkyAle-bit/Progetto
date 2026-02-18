package com.project.tesi.service;

import com.project.tesi.enums.BookingStatus;
import com.project.tesi.model.*;
import com.project.tesi.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public Booking bookSlot(Long userId, Long slotId) {
        // 1. Recupera Slot e Abbonamento
        Slot slot = slotRepository.findById(slotId).orElseThrow();
        Subscription sub = subscriptionRepository.findBySubscriberIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new RuntimeException("Abbonamento non attivo"));

        // 2. Controllo Concorrenza (Se è già prenotato)
        if (slot.isBooked()) {
            throw new IllegalStateException("Slot già occupato.");
        }

        // 3. Controllo e Scalo Crediti
        boolean isPT = slot.getProfessional().getRole().toString().equals("PERSONAL_TRAINER");
        if (isPT) {
            if (sub.getCurrentCreditsPT() <= 0) throw new RuntimeException("Crediti PT esauriti");
            sub.setCurrentCreditsPT(sub.getCurrentCreditsPT() - 1);
        } else {
            if (sub.getCurrentCreditsNutri() <= 0) throw new RuntimeException("Crediti Nutrizionista esauriti");
            sub.setCurrentCreditsNutri(sub.getCurrentCreditsNutri() - 1);
        }

        // 4. Blocca lo Slot (Optimistic Locking gestito da JPA @Version su Slot)
        slot.setBooked(true);
        slotRepository.save(slot); // Qui scatterebbe l'eccezione se due utenti premono insieme

        // 5. Generazione Link Google Meet
        String meetingLink = generateGoogleMeetLink();

        // 6. Crea Prenotazione
        Booking booking = Booking.builder()
                .client(sub.getSubscriber())
                .slot(slot)
                .status(BookingStatus.CONFIRMED)
                .meetingLink(meetingLink) // Salviamo il link
                .build();

        return bookingRepository.save(booking);
    }

    private String generateGoogleMeetLink() {
        // Per la tesi usiamo un generatore deterministico o randomico 'sicuro'
        // In produzione useresti le API Google Calendar
        return "https://meet.google.com/lookup/" + UUID.randomUUID().toString().substring(0, 10);
    }
}