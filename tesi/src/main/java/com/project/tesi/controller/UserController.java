package com.project.tesi.controller;

import com.project.tesi.dto.response.ClientBasicInfoResponse;
import com.project.tesi.dto.response.ClientDashboardResponse;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.tesi.dto.request.ProfileUpdateRequest;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Il client vede i suoi dati e i PT/Nutrizionisti che lo seguono
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<ClientDashboardResponse> getDashboard(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getClientDashboard(userId));
    }

    // Il professionista vede i clienti associati
    @GetMapping("/{userId}/clients")
    public ResponseEntity<java.util.List<ClientBasicInfoResponse>> getClientsForProfessional(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userService.getClientsForProfessional(userId));
    }

    // Aggiornamento profilo utente
    @PutMapping("/{userId}/profile")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody ProfileUpdateRequest request) {
        userService.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    // Restituisce l'account Admin per avviare chat
    @GetMapping("/admin")
    public ResponseEntity<ClientBasicInfoResponse> getAdmin() {
        return ResponseEntity.ok(userService.getAdmin());
    }
}