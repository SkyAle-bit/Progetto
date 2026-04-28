package com.project.tesi.facade;

import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;

/**
 * Mapper condiviso per convertire le entity di dominio in DTO tipizzati
 * da restituire tramite i controller amministrativi/moderatori.
 *
 * Sostituisce le precedenti conversioni Map&lt;String, Object&gt; → DTO,
 * eliminando cast non sicuri a runtime e garantendo type-safety a compile-time.
 */
public class FacadeMapper {

    private FacadeMapper() {
        // Utility class — non istanziabile
    }

    public static UserResponseDTO mapToUserResponse(User user) {
        if (user == null) return null;

        String assignedPTName = null;
        if (user.getAssignedPT() != null) {
            assignedPTName = user.getAssignedPT().getFirstName() + " " + user.getAssignedPT().getLastName();
        }

        String assignedNutriName = null;
        if (user.getAssignedNutritionist() != null) {
            assignedNutriName = user.getAssignedNutritionist().getFirstName() + " "
                    + user.getAssignedNutritionist().getLastName();
        }

        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null,
                user.getProfessionalBio(),
                assignedPTName,
                assignedNutriName
        );
    }

    public static SubscriptionResponseDTO mapToSubscriptionResponse(Subscription s) {
        if (s == null) return null;
        return new SubscriptionResponseDTO(
                s.getId(),
                s.getUser() != null ? s.getUser().getId() : null,
                s.getUser() != null ? s.getUser().getFirstName() + " " + s.getUser().getLastName() : null,
                s.getPlan() != null ? s.getPlan().getName() : "N/A",
                s.isActive(),
                s.getStartDate() != null ? s.getStartDate().toString() : null,
                s.getEndDate() != null ? s.getEndDate().toString() : null,
                s.getPlan() != null ? s.getPlan().getMonthlyInstallmentPrice() : 0.0,
                s.getCurrentCreditsPT(),
                s.getCurrentCreditsNutri()
        );
    }

    public static PlanResponseDTO mapToPlanResponse(Plan p) {
        if (p == null) return null;
        return new PlanResponseDTO(
                p.getId(),
                p.getName(),
                p.getDuration() != null ? p.getDuration().name() : null,
                p.getFullPrice(),
                p.getMonthlyInstallmentPrice(),
                p.getMonthlyCreditsPT(),
                p.getMonthlyCreditsNutri()
        );
    }
}
