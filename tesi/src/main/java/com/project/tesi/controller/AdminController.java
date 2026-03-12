package com.project.tesi.controller;

import com.project.tesi.facade.AdminFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Controller REST per le operazioni CRUD del pannello amministrativo.
 * Gestisce utenti, abbonamenti e piani commerciali.
 * Tutta la logica è delegata all'{@link AdminFacade} (pattern Facade).
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminFacade adminFacade;

    /** Restituisce la lista di tutti gli utenti registrati. */
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        return ResponseEntity.ok(adminFacade.getAllUsers());
    }

    /** Crea un nuovo utente con i dati specificati nel body. */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminFacade.createUser(body));
    }

    /** Elimina un utente, i suoi documenti e il suo abbonamento. */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        adminFacade.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Utente eliminato"));
    }

    /** Restituisce la lista di tutti gli abbonamenti (attivi e non). */
    @GetMapping("/subscriptions")
    public ResponseEntity<List<Map<String, Object>>> getAllSubscriptions() {
        return ResponseEntity.ok(adminFacade.getAllSubscriptions());
    }

    /** Crea un nuovo piano commerciale con i dati specificati nel body. */
    @PostMapping("/plans")
    public ResponseEntity<Map<String, Object>> createPlan(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(adminFacade.createPlan(body));
    }

    /** Elimina un piano commerciale (solo se non ha sottoscrittori attivi). */
    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Map<String, String>> deletePlan(@PathVariable Long id) {
        adminFacade.deletePlan(id);
        return ResponseEntity.ok(Map.of("message", "Piano eliminato"));
    }
}
