package com.project.tesi.controller;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller REST per la gestione delle recensioni.
 * Permette ai clienti di lasciare una valutazione ai professionisti
 * e di consultare le recensioni ricevute da un professionista.
 * Delega alla {@link UserFacade} (pattern Facade).
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final UserFacade userFacade;

    /** Il cliente lascia una recensione a un professionista (voto 1-5 + commento). */
    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(userFacade.addReview(request));
    }

    /** Restituisce tutte le recensioni ricevute da un professionista. */
    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsForProfessional(@PathVariable Long professionalId) {
        return ResponseEntity.ok(userFacade.getReviewsForProfessional(professionalId));
    }

    /**
     * Verifica se un cliente può recensire un professionista.
     * Restituisce {@code canReview} (true se può) e {@code hasReviewed} (true se ha già recensito).
     */
    @GetMapping("/can-review")
    public ResponseEntity<Map<String, Object>> canReview(
            @RequestParam Long clientId,
            @RequestParam Long professionalId) {
        boolean hasReviewed = userFacade.hasClientReviewed(clientId, professionalId);
        boolean can = !hasReviewed && userFacade.canClientReview(clientId, professionalId);
        return ResponseEntity.ok(Map.of("canReview", can, "hasReviewed", hasReviewed));
    }
}