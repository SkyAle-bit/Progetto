package com.project.tesi.service.impl;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.exception.common.ResourceAlreadyExistsException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.exception.review.ReviewNotAllowedException;
import com.project.tesi.model.Review;
import com.project.tesi.model.User;
import com.project.tesi.repository.ReviewRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utente", request.getUserId()));

        User professional = userRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Professionista", request.getProfessionalId()));

        // REGOLA BUSINESS: Unicità — una sola recensione per coppia
        // client-professionista
        if (reviewRepository.existsByClientIdAndProfessionalId(user.getId(), professional.getId())) {
            throw new ResourceAlreadyExistsException("Hai già lasciato una recensione per questo professionista.");
        }

        // REGOLA BUSINESS: Il cliente può recensire solo dopo 1 mese dalla
        // registrazione
        if (user.getCreatedAt() == null ||
                user.getCreatedAt().plusMonths(1).isAfter(LocalDateTime.now())) {
            throw new ReviewNotAllowedException(
                    "Puoi recensire un professionista solo dopo 1 mese dalla tua registrazione.");
        }

        Review review = Review.builder()
                .client(user)
                .professional(professional)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForProfessional(Long professionalId) {
        User professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Professionista", professionalId));

        return reviewRepository.findByProfessional(professional).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canClientReview(Long clientId, Long professionalId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", clientId));

        // Ha già recensito?
        if (reviewRepository.existsByClientIdAndProfessionalId(clientId, professionalId)) {
            return false;
        }

        // Ha passato 1 mese dalla registrazione?
        if (client.getCreatedAt() == null ||
                client.getCreatedAt().plusMonths(1).isAfter(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasClientReviewed(Long clientId, Long professionalId) {
        return reviewRepository.existsByClientIdAndProfessionalId(clientId, professionalId);
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .authorName(review.getClient().getFirstName()) // Solo nome per privacy
                .rating(review.getRating())
                .comment(review.getComment())
                .date(review.getCreatedAt())
                .build();
    }
}