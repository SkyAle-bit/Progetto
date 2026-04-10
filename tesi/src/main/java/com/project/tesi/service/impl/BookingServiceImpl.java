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
import java.time.LocalDate;

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

    private final Map<Role, BookingStrategy> strategyMap;
    private final java.time.Clock clock;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            SlotRepository slotRepository,
            UserRepository userRepository,
            SubscriptionRepository subscriptionRepository,
            BookingMapper bookingMapper,
            List<BookingStrategy> strategies,
            @org.springframework.beans.factory.annotation.Autowired(required = false) java.time.Clock clock) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.bookingMapper = bookingMapper;
        this.clock = clock != null ? clock : java.time.Clock.systemDefaultZone();
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

        BookingStrategy strategy = strategyMap.get(professional.getRole());
        if (strategy == null) {
            throw new IllegalStateException("Il professionista non è né PT né Nutrizionista");
        }

        strategy.verifyAssignment(user, professional);

        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new NoActiveSubscriptionException());

        LocalDate today = clock != null ? java.time.LocalDate.now(clock) : java.time.LocalDate.now();
        if (today.isAfter(sub.getEndDate())) {
            throw new com.project.tesi.exception.booking.SubscriptionExpiredException(
                    "Impossibile prenotare: il tuo abbonamento è già scaduto in data " + sub.getEndDate() + "."
            );
        } else if (slot.getStartTime().toLocalDate().isAfter(sub.getEndDate())) {
            throw new com.project.tesi.exception.booking.SubscriptionExpiredException(
                    "Operazione rifiutata: l'abbonamento scadrà il " + sub.getEndDate() +
                    ", prima della data prevista per questo slot (" + slot.getStartTime().toLocalDate() + ")."
            );
        }

        strategy.consumeCredits(sub);

        try {
            slot.setBooked(true);
            slotRepository.save(slot);
        } catch (OptimisticLockException e) {
            throw new SlotAlreadyBookedException("Qualcun altro ha prenotato questo slot appena prima di te. Riprova.");
        }

        try {
            subscriptionRepository.save(sub);
        } catch (OptimisticLockException e) {
            throw new com.project.tesi.exception.common.ConcurrentUpdateException("Conflitto nell'aggiornamento dei crediti dell'abbonamento. Riprova.");
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

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione", bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new com.project.tesi.exception.booking.BookingCancellationException("Non puoi annullare una prenotazione che non ti appartiene.");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new com.project.tesi.exception.booking.BookingCancellationException("Questa prenotazione non può essere annullata (stato: " + booking.getStatus() + ").");
        }

        Slot slot = booking.getSlot();
        if (slot.getStartTime().isBefore(java.time.LocalDateTime.now(clock).plusHours(24))) {
            throw new com.project.tesi.exception.booking.BookingCancellationException("Non è possibile annullare una prenotazione a meno di 24 ore dall'appuntamento.");
        }

        // 1. Libera lo slot
        slot.setBooked(false);
        slotRepository.save(slot);

        // 2. Riaccredita il credito all'abbonamento
        User professional = booking.getProfessional();
        BookingStrategy strategy = strategyMap.get(professional.getRole());
        if (strategy != null) {
            Subscription sub = subscriptionRepository.findByUserAndActiveTrue(booking.getUser())
                    .orElse(null);
            if (sub != null) {
                strategy.refundCredits(sub);
                subscriptionRepository.save(sub);
            }
        }

        // 3. Aggiorna lo stato della prenotazione
        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);
    }
}
