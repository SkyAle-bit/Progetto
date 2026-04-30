package com.project.tesi.dto.response;

import lombok.Data;
import java.util.List;

/**
 * DTO di risposta per la dashboard del cliente.
 * Aggrega tutte le informazioni necessarie alla pagina principale
 * del cliente in un'unica chiamata API.
 */
@Data
public class ClientDashboardResponse {

    private UserResponse profile;

    private List<ProfessionalSummaryDTO> followingProfessionals;

    private SubscriptionResponse subscription;

    private List<BookingResponse> upcomingBookings;

    private ClientDashboardResponse() {}

    public static class Builder {
        private UserResponse profile;
        private List<ProfessionalSummaryDTO> followingProfessionals;
        private SubscriptionResponse subscription;
        private List<BookingResponse> upcomingBookings;

        public Builder profile(UserResponse profile) {
            this.profile = profile;
            return this;
        }

        public Builder followingProfessionals(List<ProfessionalSummaryDTO> followingProfessionals) {
            this.followingProfessionals = followingProfessionals;
            return this;
        }

        public Builder subscription(SubscriptionResponse subscription) {
            this.subscription = subscription;
            return this;
        }

        public Builder upcomingBookings(List<BookingResponse> upcomingBookings) {
            this.upcomingBookings = upcomingBookings;
            return this;
        }

        public ClientDashboardResponse build() {
            ClientDashboardResponse obj = new ClientDashboardResponse();
            obj.profile = this.profile;
            obj.followingProfessionals = this.followingProfessionals;
            obj.subscription = this.subscription;
            obj.upcomingBookings = this.upcomingBookings;
            return obj;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
