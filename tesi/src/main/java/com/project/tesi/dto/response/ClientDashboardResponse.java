package com.project.tesi.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ClientDashboardResponse {
    private UserResponse profile;
    private List<ProfessionalSummaryDTO> followingProfessionals;

    private SubscriptionResponse subscription;
    private List<BookingResponse> upcomingBookings;
}