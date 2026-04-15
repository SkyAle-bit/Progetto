package com.project.tesi.service.strategy;

import org.springframework.stereotype.Component;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.booking.InsufficientCreditsException;
import com.project.tesi.exception.booking.ProfessionalNotAssignedException;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;

/**
 * Strategia di prenotazione specifica per il Nutrizionista.
 *
 * Verifica che il cliente abbia un Nutrizionista assegnato corrispondente
 * e che abbia crediti Nutrizionista residui nell'abbonamento.
 */
@Component
public class NutritionistBookingStrategy implements BookingStrategy {

    @Override
    public Role getSupportedRole() {
        return Role.NUTRITIONIST;
    }

    /**
     * Verifica che il Nutrizionista assegnato al cliente corrisponda al professionista richiesto.
     *
     * @throws ProfessionalNotAssignedException se il cliente non è assegnato a quel Nutrizionista
     */
    @Override
    public void verifyAssignment(User client, User professional) {
        if (client.getAssignedNutritionist() == null
                || !client.getAssignedNutritionist().getId().equals(professional.getId())) {
            throw new ProfessionalNotAssignedException("Nutrizionista");
        }
    }

    /**
     * Verifica che i crediti Nutrizionista siano sufficienti e ne scala uno.
     *
     * @throws InsufficientCreditsException se i crediti Nutrizionista sono esauriti
     */
    @Override
    public void consumeCredits(Subscription subscription) {
        if (subscription.getCurrentCreditsNutri() <= 0) {
            throw new InsufficientCreditsException("Nutrizionista");
        }
        subscription.setCurrentCreditsNutri(subscription.getCurrentCreditsNutri() - 1);
    }

    @Override
    public void refundCredits(Subscription subscription) {
        subscription.setCurrentCreditsNutri(subscription.getCurrentCreditsNutri() + 1);
    }
}
