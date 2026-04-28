package com.project.tesi.controller;

import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.facade.ModeratorFacade;
import lombok.RequiredArgsConstructor;
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
 * Controller REST per le operazioni del moderatore.
 */
@RestController
@RequestMapping("/api/moderator")
@RequiredArgsConstructor
public class ModeratorController {

    private final ModeratorFacade moderatorFacade;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getManageableUsers() {
        return ResponseEntity.ok(moderatorFacade.getManageableUsers());
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionResponseDTO>> getAllSubscriptions() {
        return ResponseEntity.ok(moderatorFacade.getAllSubscriptions());
    }

    @GetMapping("/chat-contacts")
    public ResponseEntity<List<UserResponseDTO>> getChatContacts() {
        return ResponseEntity.ok(moderatorFacade.getChatContacts());
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO body) {
        return ResponseEntity.ok(moderatorFacade.createUser(body));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(moderatorFacade.updateUser(id, body));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        moderatorFacade.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Utente eliminato"));
    }

    @PutMapping("/subscriptions/{id}/credits")
    public ResponseEntity<SubscriptionResponseDTO> updateSubscriptionCredits(@PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(moderatorFacade.updateSubscriptionCredits(
                id,
                body.getOrDefault("creditsPT", 0),
                body.getOrDefault("creditsNutri", 0)));
    }
}
