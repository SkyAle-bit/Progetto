package com.project.tesi.controller;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST per la gestione degli abbonamenti dei clienti.
 * Permette l'attivazione di un nuovo piano e la consultazione
 * dello stato dell'abbonamento (crediti residui, scadenza).
 * Delega alla {@link UserFacade} (pattern Facade).
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserFacade userFacade;

    /** Attiva un nuovo abbonamento per il cliente con il piano e la modalità di pagamento scelti. */
    @PostMapping("/activate")
    public ResponseEntity<SubscriptionResponse> activateSubscription(@RequestBody PlanRequest request) {
        return ResponseEntity.ok(userFacade.activateSubscription(request));
    }

    /** Restituisce lo stato dell'abbonamento attivo di un utente (crediti, scadenza, piano). */
    @GetMapping("/user/{userId}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(userFacade.getSubscriptionStatus(userId));
    }
}