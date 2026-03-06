package com.project.tesi.service.strategy;

import com.project.tesi.enums.Role;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;

public interface BookingStrategy {

    /**
     * Ritorna il ruolo supportato da questa strategia.
     */
    Role getSupportedRole();

    /**
     * Verifica che il cliente sia effettivamente assegnato a quel professionista.
     * 
     * @param client       L'utente cliente
     * @param professional L'utente professionista
     * @throws IllegalStateException se non c'è corrispondenza.
     */
    void verifyAssignment(User client, User professional);

    /**
     * Verifica e deduce i crediti corrispondenti al professionista dall'abbonamento
     * del cliente.
     * 
     * @param subscription L'abbonamento del cliente
     * @throws IllegalStateException se i crediti sono esauriti.
     */
    void consumeCredits(Subscription subscription);
}
