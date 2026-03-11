package com.project.tesi.controller;

import com.project.tesi.facade.AdminFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST per le statistiche del pannello amministrativo.
 * Restituisce un aggregato di dati per la dashboard admin
 * (utenti per ruolo, fatturato, crediti, prenotazioni, ecc.).
 * Delega all'{@link AdminFacade} (pattern Facade).
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminFacade adminFacade;

    /** Restituisce tutte le statistiche aggregate per la dashboard admin. */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminFacade.getAdminStats());
    }
}
