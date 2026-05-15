package com.project.tesi.facade;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.request.ProfileUpdateRequest;
import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ActivityFeedItemResponse;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.dto.response.ClientBasicInfoResponse;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.dto.response.ClientDashboardResponse;
import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.dto.response.SlotDTO;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.dto.response.stats.ProfessionalStatsResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.service.ActivityFeedService;
import com.project.tesi.service.BookingService;
import com.project.tesi.service.ProfessionalStatsService;
import com.project.tesi.service.ReviewService;
import com.project.tesi.service.SlotService;
import com.project.tesi.service.SubscriptionService;
import com.project.tesi.service.UserService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Facade principale per l'area utente (clienti e professionisti).
 * Coordina ben 7 servizi diversi (Booking, Review, Subscription, ecc.),
 * fornendo un punto d'ingresso unico e pulito per il controller.
 */
@Component
public class UserFacade {

    private final UserService userService;
    private final BookingService bookingService;
    private final ReviewService reviewService;
    private final SubscriptionService subscriptionService;
    private final ActivityFeedService activityFeedService;
    private final ProfessionalStatsService professionalStatsService;
    private final SlotService slotService;

    public UserFacade(UserService userService,
                      BookingService bookingService,
                      ReviewService reviewService,
                      SubscriptionService subscriptionService,
                      ActivityFeedService activityFeedService,
                      ProfessionalStatsService professionalStatsService,
                      SlotService slotService) {
        this.userService = userService;
        this.bookingService = bookingService;
        this.reviewService = reviewService;
        this.subscriptionService = subscriptionService;
        this.activityFeedService = activityFeedService;
        this.professionalStatsService = professionalStatsService;
        this.slotService = slotService;
    }


    public ClientDashboardResponse getClientDashboard(Long userId) {
        return userService.getClientDashboard(userId);
    }

    public ClientBasicInfoResponse getAdmin() {
        return userService.getAdmin();
    }


    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        userService.updateProfile(userId, request);
    }


    public List<ClientBasicInfoResponse> getClientsForProfessional(Long professionalId) {
        return userService.getClientsForProfessional(professionalId);
    }


    public BookingResponse createBooking(BookingRequest request, Long userId) {
        return bookingService.createBooking(request, userId);
    }


    public void cancelBooking(Long bookingId, Long userId) {
        bookingService.cancelBooking(bookingId, userId);
    }


    public ReviewResponse addReview(ReviewRequest request, Long userId) {
        return reviewService.addReview(request, userId);
    }


    public List<ReviewResponse> getReviewsForProfessional(Long professionalId) {
        return reviewService.getReviewsForProfessional(professionalId);
    }


    public boolean canClientReview(Long clientId, Long professionalId) {
        return reviewService.canClientReview(clientId, professionalId);
    }


    public boolean hasClientReviewed(Long clientId, Long professionalId) {
        return reviewService.hasClientReviewed(clientId, professionalId);
    }


    public SubscriptionResponse activateSubscription(PlanRequest request, Long userId) {
        return subscriptionService.activateSubscription(request, userId);
    }


    public SubscriptionResponse getSubscriptionStatus(Long userId) {
        return subscriptionService.getSubscriptionStatus(userId);
    }


    public List<ProfessionalSummaryDTO> findAvailableProfessionals(Role role) {
        return userService.findAvailableProfessionals(role);
    }


    public List<SlotDTO> getAvailableSlots(Long professionalId) {
        return slotService.getAvailableSlots(professionalId);
    }


    public List<SlotDTO> createSlots(Long professionalId, List<SlotDTO> slots) {
        return slotService.createSlots(professionalId, slots);
    }


    public void deleteSlot(Long slotId) {
        slotService.deleteSlot(slotId);
    }


    public ProfessionalStatsResponse getProfessionalStats(Long professionalId) {
        return professionalStatsService.getProfessionalStats(professionalId);
    }


    public List<ActivityFeedItemResponse> getActivityFeed(Long userId, int days, int size) {
        return activityFeedService.getActivityFeed(userId, days, size);
    }
}
