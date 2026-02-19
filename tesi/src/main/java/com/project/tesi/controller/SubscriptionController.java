package com.project.tesi.controller;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // Il cliente acquista/attiva un nuovo abbonamento
    @PostMapping("/activate")
    public ResponseEntity<SubscriptionResponse> activateSubscription(@RequestBody PlanRequest request) {
        return ResponseEntity.ok(subscriptionService.activateSubscription(request));
    }

    // Il cliente controlla i suoi crediti residui e la scadenza
    @GetMapping("/user/{userId}")
    public ResponseEntity<SubscriptionResponse> getSubscriptionStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionStatus(userId));
    }
}