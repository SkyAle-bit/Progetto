package com.project.tesi.controller;

import com.project.tesi.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST per il feed delle attività recenti di un utente.
 * Mostra prenotazioni e documenti caricati negli ultimi giorni,
 * differenziando il contenuto in base al ruolo (CLIENT o professionista).
 * Delega alla {@link UserFacade} (pattern Facade).
 */
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityFeedController {

    private final UserFacade userFacade;

    /**
     * Restituisce il feed delle attività recenti di un utente.
     *
     * @param userId ID dell'utente
     * @param days   numero di giorni da considerare (default 14)
     * @param limit  numero massimo di attività da restituire (default 15)
     * @return lista di attività ordinate dalla più recente
     */
    @GetMapping("/feed/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getActivityFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "14") int days,
            @RequestParam(defaultValue = "15") int limit) {
        return ResponseEntity.ok(userFacade.getActivityFeed(userId, days, limit));
    }
}
