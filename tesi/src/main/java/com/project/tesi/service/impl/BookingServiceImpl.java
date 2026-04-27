package com.project.tesi.service.impl;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.booking.BookingCancellationException;
import com.project.tesi.exception.booking.NoActiveSubscriptionException;
import com.project.tesi.exception.booking.SlotAlreadyBookedException;
import com.project.tesi.exception.booking.SubscriptionExpiredException;
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
import com.project.tesi.service.VideoConferenceService;
import com.project.tesi.service.strategy.BookingStrategy;
import com.project.tesi.enums.EventType;
import com.project.tesi.observer.manager.EventManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Gestisce il ciclo di vita delle prenotazioni tra clienti e professionisti.
 * Assicura la validità delle richieste controllando abbonamenti, crediti e conflitti di concorrenza.
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BookingMapper bookingMapper;
    private final List<BookingStrategy> strategies;
    private final VideoConferenceService videoConferenceService;
    private final EventManager eventManager;

    /**
     * Valida e registra una nuova prenotazione.
     * Il processo assicura che le policy di business siano rispettate: disponibilità effettiva dello slot (gestendo 
     * scenari di concorrenza tramite locking ottimistico), copertura temporale dell'abbonamento e adeguatezza dei crediti.
     * 
     * @param request I dettagli della richiesta (inclusi utente e slot desiderato).
     * @return La risposta contenente i dati della prenotazione e il link per la conferenza.
     */
    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utente", request.getUserId()));

        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot", request.getSlotId()));

        if (slot.isBooked()) {
            throw new SlotAlreadyBookedException("Slot non più disponibile");
        }

        if (bookingRepository.existsBySlotAndStatus(slot, BookingStatus.CONFIRMED)) {
            throw new SlotAlreadyBookedException("Esiste già una prenotazione confermata per questo slot.");
        }

        bookingRepository.deleteBySlot(slot);

        User professional = slot.getProfessional();

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

        strategy.verifyAssignment(user, professional);

        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(user)
                .orElseThrow(NoActiveSubscriptionException::new);

        LocalDate today = LocalDate.now();
        if (today.isAfter(sub.getEndDate())) {
            throw new SubscriptionExpiredException(
                    "Impossibile prenotare: il tuo abbonamento è scaduto in data " + sub.getEndDate() + "."
            );
        } else if (slot.getStartTime().toLocalDate().isAfter(sub.getEndDate())) {
            throw new SubscriptionExpiredException(
                    "Operazione rifiutata: l'abbonamento scadrà il " + sub.getEndDate() +
                    ", prima della data prevista per questo slot (" + slot.getStartTime().toLocalDate() + ")."
            );
        }

        slot.setBooked(true);
        slotRepository.save(slot);

        String meetLink = videoConferenceService.generateMeetingLink(user, professional, slot);

        Booking booking = Booking.builder()
                .user(user)
                .professional(professional)
                .slot(slot)
                .meetingLink(meetLink)
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepository.save(booking);

        eventManager.notifyListeners(EventType.BOOKING_CREATED, saved);

        return bookingMapper.toResponse(saved);
    }

    /**
     * Elabora la richiesta di annullamento di un appuntamento.
     * L'operazione è vincolata a regole temporali (es. disdetta con almeno 24h di preavviso) per tutelare i professionisti.
     * In caso di esito positivo, libera lo slot e riaccredita l'utilizzo all'utente delegando la logica di rimborso al ruolo specifico.
     * 
     * @param bookingId L'ID della prenotazione da annullare.
     * @param userId L'ID dell'utente richiedente (per verifica di sicurezza).
     */
    @Override
    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Prenotazione", bookingId));

        if (!booking.getUser().getId().equals(userId)) {
            throw new BookingCancellationException("Non puoi annullare una prenotazione che non ti appartiene.");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BookingCancellationException("Questa prenotazione non può essere annullata (stato: " + booking.getStatus() + ").");
        }

        Slot slot = booking.getSlot();
        if (slot.getStartTime().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new BookingCancellationException("Non è possibile annullare una prenotazione a meno di 24 ore dall'appuntamento.");
        }

        slot.setBooked(false);
        slotRepository.save(slot);

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

        booking.setStatus(BookingStatus.CANCELED);
        bookingRepository.save(booking);
    }
}
