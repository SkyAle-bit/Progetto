package com.project.tesi.controller;

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
    public ResponseEntity<List<Map<String, Object>>> getManageableUsers() {
        return ResponseEntity.ok(moderatorFacade.getManageableUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(moderatorFacade.createUser(body));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id,
                                                           @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(moderatorFacade.updateUser(id, body));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        moderatorFacade.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Utente eliminato"));
    }
}

