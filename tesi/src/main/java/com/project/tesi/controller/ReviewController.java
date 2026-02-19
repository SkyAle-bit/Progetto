package com.project.tesi.controller;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}