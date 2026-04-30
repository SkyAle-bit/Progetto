package com.project.tesi.facade;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ClientBasicInfoResponse;
import com.project.tesi.dto.response.ClientDashboardResponse;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.dto.request.ProfileUpdateRequest;
import java.util.Map;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import java.util.List;
import com.project.tesi.dto.response.SlotDTO;
import com.project.tesi.dto.response.ReviewResponse;

public interface IUserFacade {
    ClientDashboardResponse getClientDashboard(Long userId);
    void updateProfile(Long userId, ProfileUpdateRequest request);
    List<ClientBasicInfoResponse> getClientsForProfessional(Long professionalId);
    ClientBasicInfoResponse getAdmin();
    BookingResponse createBooking(BookingRequest request);
    void cancelBooking(Long bookingId, Long userId);
    ReviewResponse addReview(ReviewRequest request);
    List<ReviewResponse> getReviewsForProfessional(Long professionalId);
    boolean canClientReview(Long clientId, Long professionalId);
    boolean hasClientReviewed(Long clientId, Long professionalId);
    SubscriptionResponse activateSubscription(com.project.tesi.dto.request.PlanRequest request);
    SubscriptionResponse getSubscriptionStatus(Long userId);
    List<ProfessionalSummaryDTO> findAvailableProfessionals(Role role);
    List<SlotDTO> getAvailableSlots(Long professionalId);
    List<SlotDTO> createSlots(Long professionalId, List<SlotDTO> slots);
    void deleteSlot(Long slotId);
    List<Map<String, Object>> getActivityFeed(Long userId, int days, int limit);
    Map<String, Object> getProfessionalStats(Long professionalId);
}
