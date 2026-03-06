package com.project.tesi.service.strategy;

import org.springframework.stereotype.Component;
import com.project.tesi.enums.Role;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;

@Component
public class NutritionistBookingStrategy implements BookingStrategy {

    @Override
    public Role getSupportedRole() {
        return Role.NUTRITIONIST;
    }

    @Override
    public void verifyAssignment(User client, User professional) {
        if (client.getAssignedNutritionist() == null
                || !client.getAssignedNutritionist().getId().equals(professional.getId())) {
            throw new IllegalStateException("Non sei assegnato a questo Nutrizionista");
        }
    }

    @Override
    public void consumeCredits(Subscription subscription) {
        if (subscription.getCurrentCreditsNutri() <= 0) {
            throw new IllegalStateException("Crediti Nutrizionista esauriti");
        }
        subscription.setCurrentCreditsNutri(subscription.getCurrentCreditsNutri() - 1);
    }
}
