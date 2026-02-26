package com.project.tesi.service.impl;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.Booking.SlotAlreadyBookedException;
import com.project.tesi.exception.user.ResourceNotFoundException;
import com.project.tesi.mapper.BookingMapper;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Slot;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.SlotRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.BookingService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato"));

        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot non trovato"));

        // 1. Controllo disponibilità slot
        if (slot.isBooked()) {
            throw new SlotAlreadyBookedException("Slot non più disponibile");
        }

        // 2. Controllo che il professionista dello slot sia quello assegnato al cliente
        User professional = slot.getProfessional();
        if (professional.getRole() == Role.PERSONAL_TRAINER) {
            if (user.getAssignedPT() == null || !user.getAssignedPT().getId().equals(professional.getId())) {
                throw new IllegalStateException("Non sei assegnato a questo Personal Trainer");
            }
        } else if (professional.getRole() == Role.NUTRITIONIST) {
            if (user.getAssignedNutritionist() == null
                    || !user.getAssignedNutritionist().getId().equals(professional.getId())) {
                throw new IllegalStateException("Non sei assegnato a questo Nutrizionista");
            }
        } else {
            throw new IllegalStateException("Il professionista non è né PT né Nutrizionista");
        }

        // 3. Controllo abbonamento e crediti
        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new IllegalStateException("Nessun abbonamento attivo trovato"));

        if (professional.getRole() == Role.PERSONAL_TRAINER) {
            if (sub.getCurrentCreditsPT() <= 0)
                throw new IllegalStateException("Crediti PT esauriti");
            sub.setCurrentCreditsPT(sub.getCurrentCreditsPT() - 1);
        } else {
            if (sub.getCurrentCreditsNutri() <= 0)
                throw new IllegalStateException("Crediti Nutrizionista esauriti");
            sub.setCurrentCreditsNutri(sub.getCurrentCreditsNutri() - 1);
        }

        // 4. Occupazione slot con optimistic locking
        try {
            slot.setBooked(true);
            slotRepository.save(slot);
            subscriptionRepository.save(sub);
        } catch (OptimisticLockException e) {
            throw new SlotAlreadyBookedException("Qualcun altro ha prenotato questo slot appena prima di te. Riprova.");
        }

        // 5. Generazione link Jitsi
        String meetLink = "https://meet.jit.si/SkyAle_Consulto_" + user.getId() + "_" + professional.getId() + "_"
                + UUID.randomUUID().toString().substring(0, 8);

        Booking booking = Booking.builder()
                .user(user)
                .professional(professional)
                .slot(slot)
                .meetingLink(meetLink)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);

        // Usa il mapper invece del metodo privato
        return bookingMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public int migrateFakeMeetLinks() {
        // Cerca tutte le prenotazioni CONFIRMED
        List<Booking> activeBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED && b.getMeetingLink() != null)
                .toList();

        int updatedCount = 0;

        for (Booking b : activeBookings) {
            String currentLink = b.getMeetingLink();

            // Il vecchio finto link UUID di Google Meet
            if (currentLink != null && currentLink.startsWith("https://meet.google.com/")) {

                String realMeetLink = "https://meet.jit.si/SkyAle_Consulto_" + b.getUser().getId() + "_"
                        + b.getProfessional().getId() + "_" + UUID.randomUUID().toString().substring(0, 8);

                if (realMeetLink != null) {
                    b.setMeetingLink(realMeetLink);
                    bookingRepository.save(b);
                    updatedCount++;
                }
            }
        }

        return updatedCount;
    }
}