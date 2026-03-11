package com.project.tesi.service.impl;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.booking.NoActiveSubscriptionException;
import com.project.tesi.exception.booking.SlotAlreadyBookedException;
import com.project.tesi.exception.common.ResourceNotFoundException;
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
import com.project.tesi.service.strategy.BookingStrategy;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio per la gestione delle prenotazioni.
 *
 * Flusso di creazione prenotazione:
 * <ol>
 *   <li>Verifica disponibilità dello slot (Optimistic Locking)</li>
 *   <li>Applica lo Strategy Pattern per verificare assegnazione e crediti</li>
 *   <li>Occupa lo slot e scala i crediti dall'abbonamento</li>
 *   <li>Genera il link Jitsi per la videochiamata</li>
 *   <li>Salva la prenotazione con stato CONFIRMED</li>
 * </ol>
 *
 * Usa il Design Pattern Strategy ({@link BookingStrategy}) per gestire
 * le regole specifiche di PT e Nutrizionista.
 */
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BookingMapper bookingMapper;

    // Mappa per le strategie del tipo di professionista (Strategy Pattern)
    private final Map<Role, BookingStrategy> strategyMap;

    @Autowired
    public BookingServiceImpl(
            BookingRepository bookingRepository,
            SlotRepository slotRepository,
            UserRepository userRepository,
            SubscriptionRepository subscriptionRepository,
            BookingMapper bookingMapper,
            List<BookingStrategy> strategies) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.bookingMapper = bookingMapper;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(BookingStrategy::getSupportedRole, strategy -> strategy));
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utente", request.getUserId()));

        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot", request.getSlotId()));

        // 1. Controllo disponibilità slot
        if (slot.isBooked()) {
            throw new SlotAlreadyBookedException("Slot non più disponibile");
        }

        User professional = slot.getProfessional();

        // --- APPLICAZIONE DELLO STRATEGY PATTERN ---
        BookingStrategy strategy = strategyMap.get(professional.getRole());
        if (strategy == null) {
            throw new IllegalStateException("Il professionista non è né PT né Nutrizionista");
        }

        // 2. Controllo che il professionista dello slot sia quello assegnato al cliente
        strategy.verifyAssignment(user, professional);

        // 3. Controllo abbonamento e crediti
        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new NoActiveSubscriptionException());

        strategy.consumeCredits(sub);
        // ---------------------------------------------

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

        return bookingMapper.toResponse(saved);
    }
}
