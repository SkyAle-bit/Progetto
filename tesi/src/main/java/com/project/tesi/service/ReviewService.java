package com.project.tesi.service;

import org.springframework.validation.annotation.Validated;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ReviewResponse;
import java.util.List;

/**
 * Interfaccia del servizio per la gestione delle recensioni.
 * Permette ai clienti di recensire i professionisti e di consultare le recensioni ricevute.
 */
@Validated
public interface ReviewService {

    /** Il cliente lascia una recensione a un professionista. */
    ReviewResponse addReview(ReviewRequest request, Long userId);

    /** Restituisce tutte le recensioni ricevute da un professionista. */
    List<ReviewResponse> getReviewsForProfessional(Long professionalId);

    /** Verifica se il cliente soddisfa i requisiti temporali per recensire. */
    boolean canClientReview(Long clientId, Long professionalId);

    /** Verifica se il cliente ha già recensito un professionista. */
    boolean hasClientReviewed(Long clientId, Long professionalId);
}