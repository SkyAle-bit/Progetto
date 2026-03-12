package com.project.tesi.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.dto.response.ClientBasicInfoResponse;
import com.project.tesi.dto.response.ClientDashboardResponse;
import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.booking.ProfessionalSoldOutException;
import com.project.tesi.exception.common.ResourceAlreadyExistsException;
import com.project.tesi.exception.common.ResourceNotFoundException;
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

/**
 * Implementazione del servizio per la gestione degli utenti.
 *
 * Gestisce:
 * <ul>
 *   <li>Registrazione nuovi clienti con assegnazione professionisti (round-robin)</li>
 *   <li>Creazione automatica dell'abbonamento alla registrazione</li>
 *   <li>Dashboard cliente con profilo, professionisti, abbonamento e prossimi appuntamenti</li>
 *   <li>Lista clienti per professionisti</li>
 *   <li>Aggiornamento profilo (nome, cognome, password, immagine)</li>
 *   <li>Vetrina professionisti disponibili con media voti</li>
 * </ul>
 */
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
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void updateProfile(Long userId, com.project.tesi.dto.request.ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", userId));

        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            user.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            user.setLastName(request.getLastName().trim());
        }
        if (request.getProfilePicture() != null && !request.getProfilePicture().trim().isEmpty()) {
            user.setProfilePicture(request.getProfilePicture().trim());
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Utente", "email", request.getEmail());
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
                    .orElseThrow(() -> new ResourceNotFoundException("Piano", request.getSelectedPlanId()));

            Subscription subscription = subscriptionMapper.toSubscription(request, savedUser, selectedPlan);
            subscriptionRepository.save(subscription);
        }

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessionalSummaryDTO> findAvailableProfessionals(Role role) {
        return userRepository.findByRole(role).stream()
                .map(pro -> {
                    Double avg = reviewRepository.getAverageRating(pro.getId());
                    long activeClients;
                    if (pro.getRole() == Role.PERSONAL_TRAINER) {
                        activeClients = userRepository.countByAssignedPT(pro);
                    } else {
                        activeClients = userRepository.countByAssignedNutritionist(pro);
                    }
                    return ProfessionalSummaryDTO.builder()
                            .id(pro.getId())
                            .fullName(pro.getFirstName() + " " + pro.getLastName())
                            .role(pro.getRole())
                            .averageRating(avg != null ? avg : 0.0)
                            .currentActiveClients((int) activeClients)
                            .isSoldOut(activeClients >= 10)
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
                .orElseThrow(() -> new ResourceNotFoundException("Utente", userId));

        // Se è un professionista (PT o Nutrizionista)
        if (user.getRole() == Role.PERSONAL_TRAINER || user.getRole() == Role.NUTRITIONIST) {
            log.info("User is a professional, returning professional view");

            // Recupera le prenotazioni in cui è il professionista (non il cliente)
            List<Booking> proBookings = bookingRepository.findByProfessional(user);
            List<BookingResponse> proBookingResponses = proBookings.stream()
                    .map(bookingMapper::toResponse)
                    .collect(Collectors.toList());

            // Costruisce una dashboard semplificata (senza abbonamento e senza
            // followingProfessionals)
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

    // --- Metodi privati di supporto (Aggiornato per accettare User invece di
    // UserBuilder) ---

    private void assignProfessional(User user, Long proId, Role expectedRole) {
        if (proId == null) {
            throw new IllegalArgumentException("Devi selezionare un " + expectedRole);
        }

        User professional = userRepository.findById(proId)
                .orElseThrow(() -> new ResourceNotFoundException("Professionista", proId));

        if (professional.getRole() != expectedRole) {
            throw new IllegalArgumentException("L'ID fornito non corrisponde a un " + expectedRole + ".");
        }

        long activeClients;
        if (expectedRole == Role.PERSONAL_TRAINER) {
            activeClients = userRepository.countByAssignedPT(professional);
        } else {
            activeClients = userRepository.countByAssignedNutritionist(professional);
        }
        if (activeClients >= 10) {
            throw new ProfessionalSoldOutException(professional.getFirstName());
        }

        // Ora usiamo i classici "Setter" sull'oggetto User
        if (expectedRole == Role.PERSONAL_TRAINER) {
            user.setAssignedPT(professional);
        } else {
            user.setAssignedNutritionist(professional);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientBasicInfoResponse> getClientsForProfessional(Long professionalId) {
        User professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Professionista", professionalId));

        List<User> clients;
        if (professional.getRole() == Role.PERSONAL_TRAINER) {
            clients = userRepository.findByAssignedPT(professional);
        } else if (professional.getRole() == Role.NUTRITIONIST) {
            clients = userRepository.findByAssignedNutritionist(professional);
        } else {
            throw new IllegalArgumentException("L'utente non è un professionista");
        }

        return clients.stream()
                .map(client -> ClientBasicInfoResponse.builder()
                        .id(client.getId())
                        .firstName(client.getFirstName())
                        .lastName(client.getLastName())
                        .email(client.getEmail())
                        .profilePictureUrl(client.getProfilePicture() != null ? client.getProfilePicture()
                                : client.getProfilePictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientBasicInfoResponse getAdmin() {
        return userRepository.findByRole(Role.ADMIN).stream().findFirst()
                .map(admin -> ClientBasicInfoResponse.builder()
                        .id(admin.getId())
                        .firstName(admin.getFirstName())
                        .lastName(admin.getLastName())
                        .email(admin.getEmail())
                        .profilePictureUrl(admin.getProfilePicture() != null ? admin.getProfilePicture()
                                : admin.getProfilePictureUrl())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException("Amministratore non trovato nel sistema."));
    }
}