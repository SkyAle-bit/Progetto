package com.project.tesi.service.impl;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.Role;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Slot;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.SlotRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot non trovato"));

        // 1. Controllo Disponibilità Slot
        if (slot.isBooked()) {
            throw new RuntimeException("Slot non più disponibile");
        }

        // 2. Controllo Abbonamento e Crediti
        Subscription sub = subscriptionRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new RuntimeException("Nessun abbonamento attivo trovato"));

        Role proRole = slot.getProfessional().getRole();
        if (proRole == Role.PERSONAL_TRAINER) {
            if (sub.getCurrentCreditsPT() <= 0) throw new RuntimeException("Crediti PT esauriti");
            sub.setCurrentCreditsPT(sub.getCurrentCreditsPT() - 1);
        } else if (proRole == Role.NUTRITIONIST) {
            if (sub.getCurrentCreditsNutri() <= 0) throw new RuntimeException("Crediti Nutrizionista esauriti");
            sub.setCurrentCreditsNutri(sub.getCurrentCreditsNutri() - 1);
        }

        // 3. Occupazione Slot e Salvataggio (Optimistic Locking)
        slot.setBooked(true);
        slotRepository.save(slot);
        subscriptionRepository.save(sub);

        // 4. Generazione Link Meet (Simulato)
        String meetLink = "https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 10);

        // 5. Creazione Prenotazione
        Booking booking = Booking.builder()
                .user(user)
                .professional(slot.getProfessional())
                .slot(slot)
                .meetingLink(meetLink)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);

        return mapToResponse(saved);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .startTime(booking.getSlot().getStartTime())
                .professionalName(booking.getProfessional().getLastName())
                .userName(booking.getUser().getFirstName())
                .meetingLink(booking.getMeetingLink())
                .status(booking.getStatus())
                .canJoin(false) // Logica da implementare (es. if now > start - 10min)
                .build();
    }
}