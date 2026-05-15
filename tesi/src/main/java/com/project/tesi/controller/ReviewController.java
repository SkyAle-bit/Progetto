package com.project.tesi.controller;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Endpoint REST per le recensioni. Permette ai clienti di valutare i professionisti post-appuntamento.
 */
@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final UserFacade userFacade;

    /** Il cliente autenticato lascia una recensione a un professionista (voto 1-5 + commento). */
    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@RequestBody ReviewRequest request,
                                                     @AuthenticationPrincipal User user) {
        log.info("Aggiunta recensione per professionista {} da utente {}", request.professionalId(), user.getId());
        return ResponseEntity.ok(userFacade.addReview(request, user.getId()));
    }

    /** Restituisce tutte le recensioni ricevute da un professionista. */
    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsForProfessional(@PathVariable Long professionalId) {
        return ResponseEntity.ok(userFacade.getReviewsForProfessional(professionalId));
    }

    /**
     * Verifica se l'utente autenticato può recensire un professionista.
     * Restituisce {@code canReview} (true se può) e {@code hasReviewed} (true se ha già recensito).
     */
    @GetMapping("/can-review")
    public ResponseEntity<Map<String, Object>> canReview(@AuthenticationPrincipal User user,
                                                          @RequestParam Long professionalId) {
        boolean hasReviewed = userFacade.hasClientReviewed(user.getId(), professionalId);
        boolean can = !hasReviewed && userFacade.canClientReview(user.getId(), professionalId);
        return ResponseEntity.ok(Map.of("canReview", can, "hasReviewed", hasReviewed));
    }
}
