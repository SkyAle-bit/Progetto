package com.project.tesi.controller;

import com.project.tesi.dto.response.ClientBasicInfoResponse;
import com.project.tesi.dto.response.ClientDashboardResponse;
import com.project.tesi.dto.request.ProfileUpdateRequest;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.facade.IUserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint REST per il profilo utente. Gestisce i dati anagrafici e il recupero della dashboard cliente.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserFacade userFacade;

    /** Restituisce la dashboard completa del cliente (profilo, professionisti, abbonamento, prossimi appuntamenti). */
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<ClientDashboardResponse> getDashboard(@PathVariable Long userId) {
        return ResponseEntity.ok(userFacade.getClientDashboard(userId));
    }

    /** Restituisce la lista dei clienti assegnati a un professionista. */
    @GetMapping("/{userId}/clients")
    public ResponseEntity<List<ClientBasicInfoResponse>> getClientsForProfessional(
            @PathVariable Long userId) {
        return ResponseEntity.ok(userFacade.getClientsForProfessional(userId));
    }

    /** Aggiorna il profilo dell'utente (nome, cognome, password, immagine profilo). */
    @PutMapping("/{userId}/profile")
    public ResponseEntity<Void> updateProfile(@PathVariable Long userId, @RequestBody ProfileUpdateRequest request) {
        userFacade.updateProfile(userId, request);
        return ResponseEntity.ok().build();
    }

    /** Restituisce i dati dell'account Admin (usato dal client per avviare una chat con il supporto). */
    @GetMapping("/admin")
    public ResponseEntity<ClientBasicInfoResponse> getAdmin() {
        return ResponseEntity.ok(userFacade.getAdmin());
    }
}