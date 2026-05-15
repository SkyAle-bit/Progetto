package com.project.tesi.controller;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint REST per la gestione abbonamenti e crediti residui.
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserFacade userFacade;

    /** Attiva un nuovo abbonamento per il cliente autenticato con il piano e la modalità di pagamento scelti. */
    @PostMapping("/activate")
    public ResponseEntity<SubscriptionResponse> activateSubscription(@RequestBody PlanRequest request,
                                                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userFacade.activateSubscription(request, user.getId()));
    }

    /** Restituisce lo stato dell'abbonamento attivo dell'utente autenticato (crediti, scadenza, piano). */
    @GetMapping("/status")
    public ResponseEntity<SubscriptionResponse> getSubscriptionStatus(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userFacade.getSubscriptionStatus(user.getId()));
    }
}
