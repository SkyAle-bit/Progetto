package com.project.tesi.controller;

import com.project.tesi.facade.IAdminFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint REST per le statistiche admin. Aggrega i dati globali della piattaforma.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminStatsController {

    private final IAdminFacade adminFacade;

    /** Restituisce tutte le statistiche aggregate per la dashboard admin. */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(adminFacade.getAdminStats());
    }
}
