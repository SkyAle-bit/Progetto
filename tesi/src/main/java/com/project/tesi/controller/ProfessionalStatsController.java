package com.project.tesi.controller;

import com.project.tesi.facade.IUserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint REST per le metriche della dashboard del professionista.
 */
@RestController
@RequestMapping("/api/professional")
@RequiredArgsConstructor
public class ProfessionalStatsController {

    private final IUserFacade userFacade;

    /** Restituisce tutte le statistiche aggregate per la dashboard del professionista. */
    @GetMapping("/stats/{professionalId}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable Long professionalId) {
        return ResponseEntity.ok(userFacade.getProfessionalStats(professionalId));
    }
}
