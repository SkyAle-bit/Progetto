package com.project.tesi.dto.response;

import com.project.tesi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta con il profilo completo di un utente.
 * Include dati anagrafici, ruolo e informazioni aggiuntive
 * a seconda del tipo di utente (cliente o professionista).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Boolean active;
    private String profilePictureUrl;

    // Campi per il cliente
    private Double weight;
    private Double height;
    private String assignedPtName;
    private String assignedNutritionistName;

    // Campi per il professionista
    private String bio;
    private String specialization;
    private Integer activeClientsCount;
    private Double averageRating;

    private UserResponse(Builder builder) {
        this.id = builder.id;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.role = builder.role;
        this.active = builder.active;
        this.profilePictureUrl = builder.profilePictureUrl;
        this.weight = builder.weight;
        this.height = builder.height;
        this.assignedPtName = builder.assignedPtName;
        this.assignedNutritionistName = builder.assignedNutritionistName;
        this.bio = builder.bio;
        this.specialization = builder.specialization;
        this.activeClientsCount = builder.activeClientsCount;
        this.averageRating = builder.averageRating;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private Role role;
        private Boolean active;
        private String profilePictureUrl;
        private Double weight;
        private Double height;
        private String assignedPtName;
        private String assignedNutritionistName;
        private String bio;
        private String specialization;
        private Integer activeClientsCount;
        private Double averageRating;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public Builder weight(Double weight) {
            this.weight = weight;
            return this;
        }

        public Builder height(Double height) {
            this.height = height;
            return this;
        }

        public Builder assignedPtName(String assignedPtName) {
            this.assignedPtName = assignedPtName;
            return this;
        }

        public Builder assignedNutritionistName(String assignedNutritionistName) {
            this.assignedNutritionistName = assignedNutritionistName;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder specialization(String specialization) {
            this.specialization = specialization;
            return this;
        }

        public Builder activeClientsCount(Integer activeClientsCount) {
            this.activeClientsCount = activeClientsCount;
            return this;
        }

        public Builder averageRating(Double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public UserResponse build() {
            return new UserResponse(this);
        }
    }
}