package com.project.tesi.service.strategy;

import com.project.tesi.enums.Role;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;

/**
 * Interfaccia Strategy per la gestione delle prenotazioni (Design Pattern Strategy).
 *
 * Ogni implementazione gestisce le regole di business specifiche di un tipo
 * di professionista (PT o Nutrizionista):
 * <ul>
 *   <li>Verifica che il cliente sia effettivamente assegnato al professionista</li>
 *   <li>Verifica e deduce i crediti dall'abbonamento del cliente</li>
 * </ul>
 *
 * @see PersonalTrainerBookingStrategy
 * @see NutritionistBookingStrategy
 */
public interface BookingStrategy {

    /**
     * Restituisce il ruolo del professionista supportato da questa strategia.
     *
     * @return il ruolo (PERSONAL_TRAINER o NUTRITIONIST)
     */
    Role getSupportedRole();

    /**
     * Verifica che il cliente sia effettivamente assegnato al professionista indicato.
     *
     * @param client       l'utente cliente
     * @param professional l'utente professionista
     * @throws com.project.tesi.exception.booking.ProfessionalNotAssignedException se non c'è corrispondenza
     */
    void verifyAssignment(User client, User professional);

    /**
     * Verifica che il cliente abbia crediti sufficienti e ne scala uno dall'abbonamento.
     *
     * @param subscription l'abbonamento attivo del cliente
     * @throws com.project.tesi.exception.booking.InsufficientCreditsException se i crediti sono esauriti
     */
    void consumeCredits(Subscription subscription);
}
