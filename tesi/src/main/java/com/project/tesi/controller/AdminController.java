package com.project.tesi.controller;

import com.project.tesi.dto.request.ModeratorUserUpdateRequest;
import com.project.tesi.dto.request.PlanCreateRequestDTO;
import com.project.tesi.dto.request.SubscriptionCreditsUpdateDTO;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.facade.IAdminFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Endpoint REST per il pannello amministrativo. Gestisce utenti, abbonamenti e piani.
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "API di amministrazione per utenti, abbonamenti e piani")
public class AdminController {

    private final IAdminFacade adminFacade;

    public AdminController(IAdminFacade adminFacade) {
        this.adminFacade = adminFacade;
    }

    @Operation(summary = "Recupera tutti gli utenti registrati")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(adminFacade.getAllUsers());
    }

    @Operation(summary = "Crea un nuovo utente")
    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO request) {
        return ResponseEntity.ok(adminFacade.createUser(request));
    }
//TODO togli sto cazzo di id
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @RequestBody ModeratorUserUpdateRequest request) {
        return ResponseEntity.ok(adminFacade.updateUser(id, request));
    }
    //TODO togli sto cazzo di id
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminFacade.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionResponseDTO>> getAllSubscriptions() {
        return ResponseEntity.ok(adminFacade.getAllSubscriptions());
    }
    //TODO togli sto cazzo di id
    @PutMapping("/subscriptions/{id}/credits")
    public ResponseEntity<SubscriptionResponseDTO> updateSubscriptionCredits(@PathVariable Long id, @RequestBody SubscriptionCreditsUpdateDTO request) {
        return ResponseEntity.ok(adminFacade.updateSubscriptionCredits(
                id,
                request.creditsPT() != null ? request.creditsPT() : 0,
                request.creditsNutri() != null ? request.creditsNutri() : 0
        ));
    }

    @PostMapping("/plans")
    public ResponseEntity<PlanResponseDTO> createPlan(@RequestBody PlanCreateRequestDTO request) {
        return ResponseEntity.ok(adminFacade.createPlan(request));
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<PlanResponseDTO> updatePlan(@PathVariable Long id, @RequestBody PlanCreateRequestDTO request) {
        return ResponseEntity.ok(adminFacade.updatePlan(id, request));
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Map<String, String>> deletePlan(@PathVariable Long id) {
        adminFacade.deletePlan(id);
        return ResponseEntity.ok(Map.of("message", "Plan deleted successfully"));
    }
}
