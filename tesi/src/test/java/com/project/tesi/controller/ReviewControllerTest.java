package com.project.tesi.controller;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.facade.UserFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test unitari per {@link ReviewController}.
 */
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock private UserFacade userFacade;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    @DisplayName("addReview — restituisce 200 con la recensione creata")
    void addReview() {
        ReviewRequest req = new ReviewRequest();
        ReviewResponse resp = ReviewResponse.builder().rating(5).authorName("Mario").build();
        when(userFacade.addReview(req)).thenReturn(resp);

        ResponseEntity<ReviewResponse> response = reviewController.addReview(req);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("getReviewsForProfessional — restituisce lista recensioni")
    void getReviewsForProfessional() {
        ReviewResponse r = ReviewResponse.builder().rating(4).build();
        when(userFacade.getReviewsForProfessional(2L)).thenReturn(List.of(r));

        ResponseEntity<List<ReviewResponse>> response = reviewController.getReviewsForProfessional(2L);

        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    @DisplayName("canReview — true quando può recensire e non ha ancora recensito")
    void canReview_canReview() {
        when(userFacade.hasClientReviewed(1L, 2L)).thenReturn(false);
        when(userFacade.canClientReview(1L, 2L)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = reviewController.canReview(1L, 2L);

        assertThat(response.getBody().get("canReview")).isEqualTo(true);
        assertThat(response.getBody().get("hasReviewed")).isEqualTo(false);
    }

    @Test
    @DisplayName("canReview — false quando ha già recensito")
    void canReview_alreadyReviewed() {
        when(userFacade.hasClientReviewed(1L, 2L)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = reviewController.canReview(1L, 2L);

        assertThat(response.getBody().get("canReview")).isEqualTo(false);
        assertThat(response.getBody().get("hasReviewed")).isEqualTo(true);
    }
}

