package com.project.tesi.controller;

import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.request.ModeratorUserUpdateRequest;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.facade.IModeratorFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Moderator", description = "API per i moderatori")
public class ModeratorController {

    private final IModeratorFacade moderatorFacade;

    public ModeratorController(IModeratorFacade moderatorFacade) {
        this.moderatorFacade = moderatorFacade;
    }

    @Operation(summary = "Recupera gli utenti gestibili")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getManageableUsers() {
        return ResponseEntity.ok(moderatorFacade.getManageableUsers());
    }

    @Operation(summary = "Recupera tutti gli abbonamenti")
    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionResponseDTO>> getAllSubscriptions() {
        return ResponseEntity.ok(moderatorFacade.getAllSubscriptions());
    }

    @Operation(summary = "Recupera i contatti per la chat")
    @GetMapping("/chat-contacts")
    public ResponseEntity<List<UserResponseDTO>> getChatContacts() {
        return ResponseEntity.ok(moderatorFacade.getChatContacts());
    }

    @Operation(summary = "Crea un nuovo utente")
    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO body) {
        return ResponseEntity.ok(moderatorFacade.createUser(body));
    }

    @Operation(summary = "Aggiorna un utente esistente")
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id,
            @Valid @RequestBody ModeratorUserUpdateRequest body) {
        return ResponseEntity.ok(moderatorFacade.updateUser(id, body));
    }

    @Operation(summary = "Elimina un utente")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        moderatorFacade.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Utente eliminato"));
    }

    @Operation(summary = "Aggiorna i crediti di un abbonamento")
    @PutMapping("/subscriptions/{id}/credits")
    public ResponseEntity<SubscriptionResponseDTO> updateSubscriptionCredits(@PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        return ResponseEntity.ok(moderatorFacade.updateSubscriptionCredits(
                id,
                body.getOrDefault("creditsPT", 0),
                body.getOrDefault("creditsNutri", 0)));
    }
}
