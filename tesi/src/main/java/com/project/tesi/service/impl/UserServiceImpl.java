package com.project.tesi.service.impl;

import com.project.tesi.dto.request.ProfileUpdateRequest;
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
import com.project.tesi.mapper.BookingMapper;
import com.project.tesi.mapper.SubscriptionMapper;
import com.project.tesi.mapper.UserMapper;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.model.Chat;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.ChatRepository;
import com.project.tesi.repository.PlanRepository;
import com.project.tesi.repository.ReviewRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.EmailService;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio utente.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ChatRepository chatRepository;
    private final ReviewRepository reviewRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final BookingMapper bookingMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequest request) {
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

        User newUser = userMapper.toUser(request);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        assignProfessional(newUser, request.getSelectedPtId(), Role.PERSONAL_TRAINER);
        assignProfessional(newUser, request.getSelectedNutritionistId(), Role.NUTRITIONIST);

        User savedUser = userRepository.save(newUser);

        if (request.getSelectedPlanId() != null && request.getPaymentFrequency() != null) {
            Plan selectedPlan = planRepository.findById(request.getSelectedPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Piano", request.getSelectedPlanId()));

            Subscription subscription = subscriptionMapper.toSubscription(request, savedUser, selectedPlan);
            subscriptionRepository.save(subscription);
        }

        try {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());
        } catch (Exception e) {
            log.warn("Impossibile inviare email di benvenuto a {}: {}", savedUser.getEmail(), e.getMessage());
        }

        return userMapper.toUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfessionalSummaryDTO> findAvailableProfessionals(Role role) {
        return userRepository.findByRole(role).stream()
                .map(pro -> {
                    Double avg = reviewRepository.getAverageRating(pro.getId());
                    long activeClients = pro.getRole() == Role.PERSONAL_TRAINER
                            ? userRepository.countByAssignedPT(pro)
                            : userRepository.countByAssignedNutritionist(pro);

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", userId));

        if (user.getRole() == Role.PERSONAL_TRAINER || user.getRole() == Role.NUTRITIONIST) {
            List<BookingResponse> proBookingResponses = bookingRepository.findByProfessional(user).stream()
                    .map(bookingMapper::toResponse)
                    .collect(Collectors.toList());

            return ClientDashboardResponse.builder()
                    .profile(userMapper.toUserResponse(user))
                    .followingProfessionals(new ArrayList<>())
                    .subscription(null)
                    .upcomingBookings(proBookingResponses)
                    .build();
        }

        List<ProfessionalSummaryDTO> followingProfessionals = new ArrayList<>();
        if (user.getAssignedPT() != null) {
            followingProfessionals.add(buildProfessionalSummary(user.getAssignedPT()));
        }
        if (user.getAssignedNutritionist() != null) {
            followingProfessionals.add(buildProfessionalSummary(user.getAssignedNutritionist()));
        }

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

        List<BookingResponse> upcomingBookings = bookingRepository.findFutureByUser(user, LocalDateTime.now()).stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());

        return ClientDashboardResponse.builder()
                .profile(userMapper.toUserResponse(user))
                .followingProfessionals(followingProfessionals)
                .subscription(subResponse)
                .upcomingBookings(upcomingBookings)
                .build();
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
            throw new IllegalArgumentException("L'utente non e un professionista");
        }

        return clients.stream()
                .map(client -> ClientBasicInfoResponse.builder()
                        .id(client.getId())
                        .firstName(client.getFirstName())
                        .lastName(client.getLastName())
                        .email(client.getEmail())
                        .profilePictureUrl(client.getProfilePicture() != null
                                ? client.getProfilePicture()
                                : client.getProfilePictureUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClientBasicInfoResponse getSupportOperator() {
        List<User> moderators = userRepository.findByRole(Role.MODERATOR);

        if (!moderators.isEmpty()) {
            Optional<User> currentUser = findAuthenticatedUser();
            if (currentUser.isPresent()) {
                User actor = currentUser.get();

                if (actor.getRole() == Role.MODERATOR || actor.getRole() == Role.ADMIN) {
                    return toBasicInfo(actor);
                }

                Optional<User> existing = findExistingOperatorConversation(actor.getId(), moderators);
                User selected = existing.orElseGet(() -> {
                    int index = ThreadLocalRandom.current().nextInt(moderators.size());
                    return moderators.get(index);
                });
                return toBasicInfo(selected);
            }

            return toBasicInfo(moderators.get(0));
        }

        throw new ResourceNotFoundException("Nessun moderatore trovato nel sistema.");
    }

    @Override
    @Transactional(readOnly = true)
    public ClientBasicInfoResponse getAdmin() {
        return userRepository.findByRole(Role.ADMIN).stream().findFirst()
                .map(this::toBasicInfo)
                .orElseThrow(() -> new ResourceNotFoundException("Amministratore non trovato nel sistema."));
    }

    private ProfessionalSummaryDTO buildProfessionalSummary(User pro) {
        return ProfessionalSummaryDTO.builder()
                .id(pro.getId())
                .fullName(pro.getFirstName() + " " + pro.getLastName())
                .role(pro.getRole())
                .build();
    }

    private void assignProfessional(User user, Long proId, Role expectedRole) {
        if (proId == null) {
            throw new IllegalArgumentException("Devi selezionare un " + expectedRole);
        }

        User professional = userRepository.findById(proId)
                .orElseThrow(() -> new ResourceNotFoundException("Professionista", proId));

        if (professional.getRole() != expectedRole) {
            throw new IllegalArgumentException("L'ID fornito non corrisponde a un " + expectedRole + ".");
        }

        long activeClients = expectedRole == Role.PERSONAL_TRAINER
                ? userRepository.countByAssignedPT(professional)
                : userRepository.countByAssignedNutritionist(professional);
        if (activeClients >= 10) {
            throw new ProfessionalSoldOutException(professional.getFirstName());
        }

        if (expectedRole == Role.PERSONAL_TRAINER) {
            user.setAssignedPT(professional);
        } else {
            user.setAssignedNutritionist(professional);
        }
    }

    private ClientBasicInfoResponse toBasicInfo(User user) {
        return ClientBasicInfoResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePicture() != null
                        ? user.getProfilePicture()
                        : user.getProfilePictureUrl())
                .build();
    }

    private Optional<User> findAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            return Optional.empty();
        }
        return userRepository.findByEmail(authentication.getName());
    }

    private Optional<User> findExistingOperatorConversation(Long userId, List<User> operators) {
        List<Chat> chats = chatRepository.findAllChatsByUserId(userId);
        if (chats == null || chats.isEmpty()) {
            return Optional.empty();
        }

        List<User> partners = chats.stream()
                .map(c -> c.getUser1().getId().equals(userId) ? c.getUser2() : c.getUser1())
                .collect(Collectors.toList());

        return partners.stream()
                .filter(p -> p.getRole() == Role.MODERATOR || p.getRole() == Role.ADMIN)
                .filter(p -> operators.stream().anyMatch(o -> o.getId().equals(p.getId())))
                .findFirst();
    }
}

