package com.project.tesi.service.impl;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.common.ResourceAlreadyExistsException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.exception.review.ReviewNotAllowedException;
import com.project.tesi.model.Review;
import com.project.tesi.model.User;
import com.project.tesi.repository.ReviewRepository;
import com.project.tesi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per {@link ReviewServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User client;
    private User professional;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        professional = User.builder().email("pt@test.com").password("pass").role(Role.PERSONAL_TRAINER).id(2L).firstName("Luca").lastName("Bianchi").build();

        client = User.builder().email("mario@test.com").password("pass").role(Role.CLIENT).id(1L).firstName("Mario").lastName("Rossi")
                .assignedPT(professional).createdAt(LocalDateTime.now().minusMonths(2)).build();

        reviewRequest = new ReviewRequest();
        reviewRequest.setUserId(1L);
        reviewRequest.setProfessionalId(2L);
        reviewRequest.setRating(5);
        reviewRequest.setComment("Ottimo professionista!");
    }

    @Test
    @DisplayName("addReview — recensione aggiunta con successo")
    void addReview_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 2L)).thenReturn(false);

        Review savedReview = Review.builder().id(1L).client(client).professional(professional)
                .rating(5).comment("Ottimo professionista!")
                .createdAt(LocalDateTime.now()).build();
        when(reviewRepository.save(any(Review.class))).thenReturn(savedReview);

        ReviewResponse response = reviewService.addReview(reviewRequest);

        assertThat(response).isNotNull();
        assertThat(response.getRating()).isEqualTo(5);
        assertThat(response.getAuthorName()).isEqualTo("Mario");
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("addReview — utente non trovato lancia ResourceNotFoundException")
    void addReview_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addReview(reviewRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("addReview — professionista non trovato lancia ResourceNotFoundException")
    void addReview_professionalNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.addReview(reviewRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("addReview — recensione duplicata lancia ResourceAlreadyExistsException")
    void addReview_alreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(userRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 2L)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.addReview(reviewRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    @DisplayName("addReview — registrazione troppo recente lancia ReviewNotAllowedException")
    void addReview_tooEarly() {
        User recentClient = User.builder().email("mario@test.com").password("pass").role(Role.CLIENT).id(1L).firstName("Mario")
                .assignedPT(professional).createdAt(LocalDateTime.now().minusDays(10)).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(recentClient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 2L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.addReview(reviewRequest))
                .isInstanceOf(ReviewNotAllowedException.class);
    }

    @Test
    @DisplayName("addReview — createdAt null lancia ReviewNotAllowedException")
    void addReview_createdAtNull() {
        User nullCreatedClient = User.builder().email("mario@test.com").password("pass").role(Role.CLIENT).id(1L).firstName("Mario")
                .assignedPT(professional).createdAt(null).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(nullCreatedClient));
        when(userRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 2L)).thenReturn(false);

        assertThatThrownBy(() -> reviewService.addReview(reviewRequest))
                .isInstanceOf(ReviewNotAllowedException.class);
    }

    @Test
    @DisplayName("getReviewsForProfessional — restituisce lista recensioni")
    void getReviewsForProfessional_success() {
        Review r = Review.builder().id(1L).client(client).professional(professional)
                .rating(4).comment("Bravo").createdAt(LocalDateTime.now()).build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(professional));
        when(reviewRepository.findByProfessional(professional)).thenReturn(List.of(r));

        List<ReviewResponse> result = reviewService.getReviewsForProfessional(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRating()).isEqualTo(4);
    }

    @Test
    @DisplayName("getReviewsForProfessional — professionista non trovato")
    void getReviewsForProfessional_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.getReviewsForProfessional(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("canClientReview — true quando registrazione > 1 mese e non ha ancora recensito")
    void canClientReview_true() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 2L)).thenReturn(false);

        assertThat(reviewService.canClientReview(1L, 2L)).isTrue();
    }

    @Test
    @DisplayName("canClientReview — false quando ha già recensito")
    void canClientReview_alreadyReviewed() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 2L)).thenReturn(true);

        assertThat(reviewService.canClientReview(1L, 2L)).isFalse();
    }

    @Test
    @DisplayName("canClientReview — false quando registrazione < 1 mese")
    void canClientReview_tooRecent() {
        User recent = User.builder().email("x@x.com").password("x").role(Role.CLIENT).id(1L).assignedPT(professional).createdAt(LocalDateTime.now().minusDays(5)).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(recent));
        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 2L)).thenReturn(false);

        assertThat(reviewService.canClientReview(1L, 2L)).isFalse();
    }

    @Test
    @DisplayName("canClientReview — false quando createdAt null")
    void canClientReview_createdAtNull() {
        User nullDate = User.builder().email("x@x.com").password("x").role(Role.CLIENT).id(1L).assignedPT(professional).createdAt(null).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(nullDate));
        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 2L)).thenReturn(false);

        assertThat(reviewService.canClientReview(1L, 2L)).isFalse();
    }

    @Test
    @DisplayName("hasClientReviewed — delega al repository")
    void hasClientReviewed() {
        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 2L)).thenReturn(true);
        assertThat(reviewService.hasClientReviewed(1L, 2L)).isTrue();

        when(reviewRepository.existsByClientIdAndProfessionalId(1L, 3L)).thenReturn(false);
        assertThat(reviewService.hasClientReviewed(1L, 3L)).isFalse();
    }
}

