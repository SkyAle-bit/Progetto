package com.project.tesi.controller;

import com.project.tesi.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST per le statistiche della dashboard del professionista.
 * Restituisce dati aggregati come clienti assegnati, appuntamenti di oggi,
 * documenti caricati nella settimana e clienti che necessitano di aggiornamenti.
 * Delega alla {@link UserFacade} (pattern Facade).
 */
@RestController
@RequestMapping("/api/professional")
@RequiredArgsConstructor
public class ProfessionalStatsController {

    private final UserFacade userFacade;

    /** Restituisce tutte le statistiche aggregate per la dashboard del professionista. */
    @GetMapping("/stats/{professionalId}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable Long professionalId) {
        return ResponseEntity.ok(userFacade.getProfessionalStats(professionalId));
    }
}
