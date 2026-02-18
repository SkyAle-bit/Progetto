package com.project.tesi.service.impl;

import com.project.tesi.dto.request.ReviewRequest;
import com.project.tesi.dto.response.ReviewResponse;
import com.project.tesi.model.Review;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.ReviewRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ReviewResponse addReview(ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        User professional = userRepository.findById(request.getProfessionalId())
                .orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        // REGOLA BUSINESS: L'utente deve aver avuto almeno una prenotazione con questo professionista
        boolean hasBooked = bookingRepository.existsByUserAndProfessional(user, professional);
        if (!hasBooked) {
            throw new RuntimeException("Non puoi recensire un professionista con cui non hai avuto appuntamenti.");
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
                .orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        return reviewRepository.findByProfessional(professional).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .authorName(review.getClient().getFirstName()) // Solo nome per privacy
                .rating(review.getRating())
                .comment(review.getComment())
                .build();
    }
}