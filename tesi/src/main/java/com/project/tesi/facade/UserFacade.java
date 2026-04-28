package com.project.tesi.facade;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.request.ProfileUpdateRequest;
import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.dto.response.ClientBasicInfoResponse;
import com.project.tesi.dto.response.ClientDashboardResponse;
import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.dto.response.SlotDTO;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.service.ActivityFeedService;
import com.project.tesi.service.BookingService;
import com.project.tesi.service.ProfessionalStatsService;
import com.project.tesi.service.ReviewService;
import com.project.tesi.service.SlotService;
import com.project.tesi.service.SubscriptionService;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Facade per l'area cliente e professionista (Design Pattern Facade).
 *
 * Fornisce un punto d'accesso unificato a tutte le operazioni lato utente,
 * coordinando 7 servizi specializzati:
 * <ul>
 * <li>{@link UserService} — profilo, dashboard, gestione clienti</li>
 * <li>{@link BookingService} — prenotazioni appuntamenti</li>
 * <li>{@link ReviewService} — recensioni ai professionisti</li>
 * <li>{@link SubscriptionService} — attivazione e stato abbonamento</li>
 * <li>{@link ActivityFeedService} — feed attività recenti</li>
 * <li>{@link ProfessionalStatsService} — statistiche dashboard
 * professionista</li>
 * <li>{@link SlotService} — gestione slot del calendario</li>
 * </ul>
 * I controller comunicano esclusivamente con questa facade.
 */
@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final SubscriptionService subscriptionService;
    private final ActivityFeedService activityFeedService;
    private final ProfessionalStatsService professionalStatsService;
    private final SlotService slotService;

    /**
     * Restituisce la dashboard completa del cliente (profilo, professionisti,
     * abbonamento, prenotazioni).
     */
    public ClientDashboardResponse getClientDashboard(Long userId) {
        return userService.getClientDashboard(userId);
    }

    /** Aggiorna il profilo dell'utente (nome, cognome, password, immagine). */
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        userService.updateProfile(userId, request);
    }

    /** Restituisce la lista dei clienti assegnati a un professionista. */
    public List<ClientBasicInfoResponse> getClientsForProfessional(Long professionalId) {
        return userService.getClientsForProfessional(professionalId);
    }

    /** Restituisce i dati dell'account Supporto o Admin (per avviare chat). */
    public ClientBasicInfoResponse getAdmin() {
        return userService.getSupportOperator();
    }

    public BookingResponse createBooking(BookingRequest request) {
        return bookingService.createBooking(request);
    }

    /**
     * Annulla una prenotazione esistente, liberando lo slot e riaccreditando il
     * credito.
     */
    public void cancelBooking(Long bookingId, Long userId) {
        bookingService.cancelBooking(bookingId, userId);
    }

    /** Il cliente lascia una recensione a un professionista. */
    public ReviewResponse addReview(ReviewRequest request) {
        return reviewService.addReview(request);
    }

    /** Restituisce tutte le recensioni ricevute da un professionista. */
    public List<ReviewResponse> getReviewsForProfessional(Long professionalId) {
        return reviewService.getReviewsForProfessional(professionalId);
    }

    /** Verifica se il cliente soddisfa i requisiti temporali per recensire. */
    public boolean canClientReview(Long clientId, Long professionalId) {
        return reviewService.canClientReview(clientId, professionalId);
    }

    /** Verifica se il cliente ha già recensito un professionista. */
    public boolean hasClientReviewed(Long clientId, Long professionalId) {
        return reviewService.hasClientReviewed(clientId, professionalId);
    }

    /** Attiva un nuovo abbonamento per il cliente. */
    public SubscriptionResponse activateSubscription(com.project.tesi.dto.request.PlanRequest request) {
        return subscriptionService.activateSubscription(request);
    }

    /** Restituisce lo stato dell'abbonamento attivo (crediti residui, scadenza). */
    public SubscriptionResponse getSubscriptionStatus(Long userId) {
        return subscriptionService.getSubscriptionStatus(userId);
    }

    /** Restituisce la lista dei professionisti disponibili per un dato ruolo. */
    public List<ProfessionalSummaryDTO> findAvailableProfessionals(Role role) {
        return userService.findAvailableProfessionals(role);
    }

    /** Restituisce gli slot liberi di un professionista. */
    public List<SlotDTO> getAvailableSlots(Long professionalId) {
        return slotService.getAvailableSlots(professionalId);
    }

    /** Crea nuovi slot nel calendario di un professionista. */
    public List<SlotDTO> createSlots(Long professionalId, List<SlotDTO> slots) {
        return slotService.createSlots(professionalId, slots);
    }

    /** Elimina uno slot dal calendario. */
    public void deleteSlot(Long slotId) {
        slotService.deleteSlot(slotId);
    }

    /** Restituisce le statistiche aggregate per la dashboard del professionista. */
    public Map<String, Object> getProfessionalStats(Long professionalId) {
        return professionalStatsService.getProfessionalStats(professionalId);
    }

    /** Restituisce il feed delle attività recenti di un utente. */
    public List<Map<String, Object>> getActivityFeed(Long userId, int days, int limit) {
        return activityFeedService.getActivityFeed(userId, days, limit);
    }
}
