package com.project.tesi.controller;

import com.project.tesi.dto.request.ProfileUpdateRequest;
import com.project.tesi.dto.response.ClientBasicInfoResponse;
import com.project.tesi.dto.response.ClientDashboardResponse;
import lombok.extern.slf4j.Slf4j;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint REST per il profilo utente. Gestisce i dati anagrafici e il recupero della dashboard cliente.
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserFacade userFacade;

    /** Restituisce la dashboard completa del cliente (profilo, professionisti, abbonamento, prossimi appuntamenti). */
    @GetMapping("/dashboard")
    public ResponseEntity<ClientDashboardResponse> getDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userFacade.getClientDashboard(user.getId()));
    }

    /** Restituisce la lista dei clienti assegnati al professionista autenticato. */
    @GetMapping("/clients")
    public ResponseEntity<List<ClientBasicInfoResponse>> getClientsForProfessional(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userFacade.getClientsForProfessional(user.getId()));
    }

    /** Aggiorna il profilo dell'utente autenticato (nome, cognome, password, immagine profilo). */
    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal User user, @RequestBody ProfileUpdateRequest request) {
        userFacade.updateProfile(user.getId(), request);
        return ResponseEntity.ok().build();
    }

    /** Restituisce le informazioni di base del primo amministratore (usato per la funzione "Contatta supporto"). */
    @GetMapping("/admin")
    public ResponseEntity<ClientBasicInfoResponse> getAdmin() {
        return ResponseEntity.ok(userFacade.getAdmin());
    }
}
