package com.project.tesi.service.impl;

import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.*;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.user.ResourceAlreadyExistsException;
import com.project.tesi.exception.user.ResourceNotFoundException;
import com.project.tesi.mapper.SubscriptionMapper;
import com.project.tesi.mapper.UserMapper;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.PlanRepository;
import com.project.tesi.repository.ReviewRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.project.tesi.mapper.BookingMapper;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    // MAPPER INIETTATI
    private final UserMapper userMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Email già registrata. Usa un'altra email o fai il login.");
        }

        // 1T. DELEGHIAMO LA CREAZIONE DELL'UTENE AL MAPPER
        User newUser = userMapper.toUser(request);

        // 2. ASSEGNIAMO I PROFESSIONISTI ALL'ENTITÀ GIÀ CREATA
        assignProfessional(newUser, request.getSelectedPtId(), Role.PERSONAL_TRAINER);
        assignProfessional(newUser, request.getSelectedNutritionistId(), Role.NUTRITIONIST);

        // Salviamo l'utente
        User savedUser = userRepository.save(newUser);

        // 3. DELEGHIAMO LA CREAZIONE DELL'ABBONAMENTO AL MAPPER
        if (request.getSelectedPlanId() != null && request.getPaymentFrequency() != null) {
            Plan selectedPlan = planRepository.findById(request.getSelectedPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Piano non trovato."));

            Subscription subscription = subscriptionMapper.toSubscription(request, savedUser, selectedPlan);
            subscriptionRepository.save(subscription);
        }

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessionalSummaryDTO> findAvailableProfessionals(Role role) {
        // ... Codice invariato ...
        return userRepository.findByRole(role).stream()
                .map(pro -> {
                    Double avg = reviewRepository.getAverageRating(pro.getId());
                    long activeClients = userRepository.countByAssignedPT(pro);
                    return ProfessionalSummaryDTO.builder()
                            .id(pro.getId())
                            .fullName(pro.getFirstName() + " " + pro.getLastName())
                            .role(pro.getRole())
                            .averageRating(avg != null ? avg : 0.0)
                            .currentActiveClients((int) activeClients)
                            .isSoldOut(activeClients >= 50)
                            .build();
                })
                .sorted((p1, p2) -> Double.compare(p2.getAverageRating(), p1.getAverageRating()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDashboardResponse getClientDashboard(Long userId) {
        log.info("getClientDashboard called for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente con ID " + userId + " non trovato."));

        // Se è un professionista (PT o Nutrizionista)
        if (user.getRole() == Role.PERSONAL_TRAINER || user.getRole() == Role.NUTRITIONIST) {
            log.info("User is a professional, returning professional view");

            // Recupera le prenotazioni in cui è il professionista (non il cliente)
            List<Booking> proBookings = bookingRepository.findByProfessional(user);
            List<BookingResponse> proBookingResponses = proBookings.stream()
                    .map(bookingMapper::toResponse)
                    .collect(Collectors.toList());

            // Costruisce una dashboard semplificata (senza abbonamento e senza followingProfessionals)
            return ClientDashboardResponse.builder()
                    .profile(userMapper.toUserResponse(user))
                    .followingProfessionals(new ArrayList<>()) // lista vuota
                    .subscription(null) // nessun abbonamento
                    .upcomingBookings(proBookingResponses) // prenotazioni come professionista
                    .build();
        }

        // Altrimenti è un cliente: procedi con la logica originale
        log.info("User is a client, returning client dashboard");

        // 1. Professionisti assegnati
        List<ProfessionalSummaryDTO> followingProfessionals = new ArrayList<>();
        if (user.getAssignedPT() != null) {
            followingProfessionals.add(buildProfessionalSummary(user.getAssignedPT()));
        }
        if (user.getAssignedNutritionist() != null) {
            followingProfessionals.add(buildProfessionalSummary(user.getAssignedNutritionist()));
        }

        // 2. Abbonamento attivo
        SubscriptionResponse subResponse = null;
        Optional<Subscription> subOpt = subscriptionRepository.findByUserAndActiveTrue(user);
        if (subOpt.isPresent()) {
            Subscription sub = subOpt.get();
            subResponse = SubscriptionResponse.builder()
                    .id(sub.getId())
                    .planName(sub.getPlan().getName())
                    .startDate(sub.getStartDate())
                    .endDate(sub.getEndDate())
                    .isActive(sub.isActive())
                    .remainingPtCredits(sub.getCurrentCreditsPT())
                    .remainingNutritionistCredits(sub.getCurrentCreditsNutri())
                    .build();
        }

        // 3. Prenotazioni future del cliente (usando la query JPQL)
        List<Booking> upcoming = bookingRepository.findFutureByUser(user, LocalDateTime.now());
        List<BookingResponse> upcomingBookings = upcoming.stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());

        // 4. Risposta finale
        return ClientDashboardResponse.builder()
                .profile(userMapper.toUserResponse(user))
                .followingProfessionals(followingProfessionals)
                .subscription(subResponse)
                .upcomingBookings(upcomingBookings)
                .build();
    }

    // --- Aggiungi questo piccolo Helper privato in fondo alla classe ---
    private ProfessionalSummaryDTO buildProfessionalSummary(User pro) {
        return ProfessionalSummaryDTO.builder()
                .id(pro.getId())
                .fullName(pro.getFirstName() + " " + pro.getLastName())
                .role(pro.getRole())
                // Eventualmente in futuro potrai aggiungerci la foto profilo
                .build();
    }

    // --- Metodi privati di supporto (Aggiornato per accettare User invece di UserBuilder) ---

    private void assignProfessional(User user, Long proId, Role expectedRole) {
        if (proId == null) {
            throw new IllegalArgumentException("Devi selezionare un " + expectedRole);
        }

        User professional = userRepository.findById(proId)
                .orElseThrow(() -> new ResourceNotFoundException("Professionista con ID " + proId + " non trovato nel sistema."));

        if (professional.getRole() != expectedRole) {
            throw new IllegalArgumentException("L'ID fornito non corrisponde a un " + expectedRole + ".");
        }

        long activeClients = userRepository.countByAssignedPT(professional);
        if (activeClients >= 50) {
            throw new IllegalStateException("Il professionista " + professional.getFirstName() + " è attualmente Sold Out.");
        }

        // Ora usiamo i classici "Setter" sull'oggetto User
        if (expectedRole == Role.PERSONAL_TRAINER) {
            user.setAssignedPT(professional);
        } else {
            user.setAssignedNutritionist(professional);
        }
    }
}