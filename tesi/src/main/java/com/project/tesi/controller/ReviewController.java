package com.project.tesi.controller;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Il cliente lascia una recensione
    @PostMapping
    public ResponseEntity<ReviewResponse> addReview(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.addReview(request));
    }

    // Mostra tutte le recensioni ricevute da un Personal Trainer o Nutrizionista
    @GetMapping("/professional/{professionalId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsForProfessional(@PathVariable Long professionalId) {
        return ResponseEntity.ok(reviewService.getReviewsForProfessional(professionalId));
    }

    // Controlla se un cliente può recensire un professionista
    @GetMapping("/can-review")
    public ResponseEntity<Map<String, Object>> canReview(
            @RequestParam Long clientId,
            @RequestParam Long professionalId) {
        boolean hasReviewed = reviewService.hasClientReviewed(clientId, professionalId);
        boolean can = !hasReviewed && reviewService.canClientReview(clientId, professionalId);
        return ResponseEntity.ok(Map.of("canReview", can, "hasReviewed", hasReviewed));
    }
}