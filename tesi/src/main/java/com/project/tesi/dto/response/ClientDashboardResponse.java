package com.project.tesi.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * DTO di risposta per la dashboard del cliente.
 * Aggrega tutte le informazioni necessarie alla pagina principale
 * del cliente in un'unica chiamata API.
 */
@Data
@Builder
public class ClientDashboardResponse {

    private UserResponse profile;

    private List<ProfessionalSummaryDTO> followingProfessionals;

    private SubscriptionResponse subscription;

    private List<BookingResponse> upcomingBookings;
}