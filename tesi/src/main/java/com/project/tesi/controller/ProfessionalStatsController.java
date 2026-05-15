package com.project.tesi.controller;

import com.project.tesi.dto.response.stats.ProfessionalStatsResponse;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint REST per le metriche della dashboard del professionista autenticato.
 */
@RestController
@RequestMapping("/api/professional")
@RequiredArgsConstructor
public class ProfessionalStatsController {

    private final UserFacade userFacade;

    /** Restituisce tutte le statistiche aggregate per la dashboard del professionista autenticato. */
    @GetMapping("/stats")
    public ResponseEntity<ProfessionalStatsResponse> getStats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userFacade.getProfessionalStats(user.getId()));
    }
}
