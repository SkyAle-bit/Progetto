package com.project.tesi.dto.response;

import com.project.tesi.enums.Role;
import lombok.Data;

/**
 * DTO di risposta con il riepilogo di un professionista.
 * Usato nella vetrina pubblica e nella dashboard del cliente
 * per mostrare i professionisti disponibili o assegnati.
 */
@Data
public class ProfessionalSummaryDTO {

    private Long id;

    private String fullName;

    private Double averageRating;

    private Integer currentActiveClients;

    private boolean isSoldOut;

    private Role role;

    private ProfessionalSummaryDTO() {}

    public static class Builder {
        private Long id;
        private String fullName;
        private Double averageRating;
        private Integer currentActiveClients;
        private boolean isSoldOut;
        private Role role;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public Builder currentActiveClients(Integer currentActiveClients) {
            this.currentActiveClients = currentActiveClients;
            return this;
        }

        public Builder isSoldOut(boolean isSoldOut) {
            this.isSoldOut = isSoldOut;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public ProfessionalSummaryDTO build() {
            ProfessionalSummaryDTO obj = new ProfessionalSummaryDTO();
            obj.id = this.id;
            obj.fullName = this.fullName;
            obj.averageRating = this.averageRating;
            obj.currentActiveClients = this.currentActiveClients;
            obj.isSoldOut = this.isSoldOut;
            obj.role = this.role;
            return obj;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
