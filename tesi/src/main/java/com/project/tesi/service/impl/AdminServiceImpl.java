package com.project.tesi.service.impl;

import com.project.tesi.enums.PlanDuration;
import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.common.ResourceAlreadyExistsException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.exception.common.UnauthorizedAccessException;
import com.project.tesi.exception.user.AdminSelfDeletionNotAllowedException;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.ChatMessageRepository;
import com.project.tesi.repository.DocumentRepository;
import com.project.tesi.repository.PlanRepository;
import com.project.tesi.repository.ReviewRepository;
import com.project.tesi.repository.SlotRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.repository.WeeklyScheduleRepository;
import com.project.tesi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio amministrativo.
 * Gestisce utenti, piani e abbonamenti con regole dedicate per admin e moderator.
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private static final Set<Role> MODERATOR_MANAGEABLE_ROLES = EnumSet.of(
            Role.CLIENT,
            Role.PERSONAL_TRAINER,
            Role.NUTRITIONIST
    );

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DocumentRepository documentRepository;
    private final BookingRepository bookingRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReviewRepository reviewRepository;
    private final SlotRepository slotRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream().map(this::toUserMap).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getModeratorManageableUsers() {
        User actor = getAuthenticatedActor();
        ensureRole(actor, Role.MODERATOR);

        return userRepository.findAll().stream()
                .filter(u -> MODERATOR_MANAGEABLE_ROLES.contains(u.getRole()))
                .map(this::toUserMap)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getModeratorChatContacts() {
        User actor = getAuthenticatedActor();
        ensureRole(actor, Role.MODERATOR);

        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN || u.getRole() == Role.INSURANCE_MANAGER)
                .map(this::toUserMap)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> createUser(Map<String, Object> body) {
        return createUserInternal(body, null);
    }

    @Override
    @Transactional
    public Map<String, Object> createUserAsModerator(Map<String, Object> body) {
        User actor = getAuthenticatedActor();
        ensureRole(actor, Role.MODERATOR);
        return createUserInternal(body, actor);
    }

    @Override
    @Transactional
    public Map<String, Object> updateUserAsModerator(Long id, Map<String, Object> body) {
        User actor = getAuthenticatedActor();
        ensureRole(actor, Role.MODERATOR);
        return updateUserInternal(id, body, actor);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        Optional<User> maybeActor = getAuthenticatedActorOptional();
        deleteUserInternal(id, maybeActor.orElse(null));
    }

    @Override
    @Transactional
    public void deleteUserAsModerator(Long id) {
        User actor = getAuthenticatedActor();
        ensureRole(actor, Role.MODERATOR);
        deleteUserInternal(id, actor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("userId", s.getUser().getId());
            map.put("userName", s.getUser().getFirstName() + " " + s.getUser().getLastName());
            map.put("planName", s.getPlan() != null ? s.getPlan().getName() : "N/A");
            map.put("active", s.isActive());
            map.put("startDate", s.getStartDate() != null ? s.getStartDate().toString() : null);
            map.put("endDate", s.getEndDate() != null ? s.getEndDate().toString() : null);
            map.put("monthlyPrice", s.getPlan() != null ? s.getPlan().getMonthlyInstallmentPrice() : 0);
            map.put("currentCreditsPT", s.getCurrentCreditsPT());
            map.put("currentCreditsNutri", s.getCurrentCreditsNutri());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> updateSubscriptionCredits(Long subscriptionId, int creditsPT, int creditsNutri) {
        User actor = getAuthenticatedActor();
        if (actor.getRole() != Role.ADMIN && actor.getRole() != Role.MODERATOR) {
            throw new UnauthorizedAccessException("Non hai i permessi per modificare i crediti.");
        }

        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Abbonamento", subscriptionId));

        sub.setCurrentCreditsPT(creditsPT);
        sub.setCurrentCreditsNutri(creditsNutri);
        Subscription saved = subscriptionRepository.save(sub);

        Map<String, Object> map = new HashMap<>();
        map.put("id", saved.getId());
        map.put("currentCreditsPT", saved.getCurrentCreditsPT());
        map.put("currentCreditsNutri", saved.getCurrentCreditsNutri());
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> createPlan(Map<String, Object> body) {
        String name = stringValue(body.get("name"));
        String durationRaw = stringValue(body.get("duration"));
        Double fullPrice = numberValue(body.get("fullPrice")) != null
                ? numberValue(body.get("fullPrice")).doubleValue()
                : null;
        Double monthlyInstallmentPrice = numberValue(body.get("monthlyInstallmentPrice")) != null
                ? numberValue(body.get("monthlyInstallmentPrice")).doubleValue()
                : null;
        Integer monthlyCreditsPT = numberValue(body.get("monthlyCreditsPT")) != null
                ? numberValue(body.get("monthlyCreditsPT")).intValue()
                : 0;
        Integer monthlyCreditsNutri = numberValue(body.get("monthlyCreditsNutri")) != null
                ? numberValue(body.get("monthlyCreditsNutri")).intValue()
                : 0;

        if (name == null || durationRaw == null || fullPrice == null || monthlyInstallmentPrice == null) {
            throw new IllegalArgumentException("Campi obbligatori mancanti (name, duration, fullPrice, monthlyInstallmentPrice).");
        }

        planRepository.findByName(name).ifPresent(existing -> {
            throw new ResourceAlreadyExistsException("Piano", "name", name);
        });

        PlanDuration duration;
        try {
            duration = PlanDuration.valueOf(durationRaw);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Durata non valida: " + durationRaw);
        }

        Plan saved = planRepository.save(Plan.builder()
                .name(name)
                .duration(duration)
                .fullPrice(fullPrice)
                .monthlyInstallmentPrice(monthlyInstallmentPrice)
                .monthlyCreditsPT(monthlyCreditsPT)
                .monthlyCreditsNutri(monthlyCreditsNutri)
                .build());

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId());
        result.put("name", saved.getName());
        result.put("duration", saved.getDuration().name());
        result.put("fullPrice", saved.getFullPrice());
        result.put("monthlyInstallmentPrice", saved.getMonthlyInstallmentPrice());
        result.put("monthlyCreditsPT", saved.getMonthlyCreditsPT());
        result.put("monthlyCreditsNutri", saved.getMonthlyCreditsNutri());
        return result;
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piano", id));

        boolean hasSubscribers = subscriptionRepository.findAll().stream()
                .anyMatch(s -> s.getPlan() != null && id.equals(s.getPlan().getId()));
        if (hasSubscribers) {
            throw new IllegalStateException("Impossibile eliminare il piano: esistono sottoscrizioni collegate.");
        }

        planRepository.delete(plan);
    }

    private Map<String, Object> createUserInternal(Map<String, Object> body, User actor) {
        String email = stringValue(body.get("email"));
        String firstName = stringValue(body.get("firstName"));
        String lastName = stringValue(body.get("lastName"));
        String password = stringValue(body.get("password"));
        String roleRaw = stringValue(body.get("role"));

        if (email == null || firstName == null || lastName == null || password == null || roleRaw == null) {
            throw new IllegalArgumentException("Campi obbligatori mancanti (email, firstName, lastName, password, role).");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResourceAlreadyExistsException("Utente", "email", email);
        }

        Role targetRole = parseRole(roleRaw);
        validateCreatePermissions(actor, targetRole);

        User user = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(encodePassword(password))
                .role(targetRole)
                .build();

        if (targetRole == Role.CLIENT) {
            Number ptIdNum = numberValue(body.get("assignedPTId"));
            if (ptIdNum != null) {
                userRepository.findById(ptIdNum.longValue()).ifPresent(user::setAssignedPT);
            }

            Number nutriIdNum = numberValue(body.get("assignedNutritionistId"));
            if (nutriIdNum != null) {
                userRepository.findById(nutriIdNum.longValue()).ifPresent(user::setAssignedNutritionist);
            }
        }

        User saved = userRepository.save(user);
        createSubscriptionIfRequested(saved, targetRole, body);

        return toUserMap(saved);
    }

    private Map<String, Object> updateUserInternal(Long id, Map<String, Object> body, User actor) {
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", id));

        validateUpdatePermissions(actor, target, body);

        String email = stringValue(body.get("email"));
        if (email != null && !email.equalsIgnoreCase(target.getEmail())) {
            userRepository.findByEmail(email)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ResourceAlreadyExistsException("Utente", "email", email);
                    });
            target.setEmail(email);
        }

        String firstName = stringValue(body.get("firstName"));
        if (firstName != null && !firstName.isBlank()) {
            target.setFirstName(firstName);
        }

        String lastName = stringValue(body.get("lastName"));
        if (lastName != null && !lastName.isBlank()) {
            target.setLastName(lastName);
        }

        String password = stringValue(body.get("password"));
        if (password != null && !password.isBlank()) {
            target.setPassword(encodePassword(password));
        }

        String roleRaw = stringValue(body.get("role"));
        if (roleRaw != null) {
            Role requestedRole = parseRole(roleRaw);
            validateRoleTransition(actor, target.getRole(), requestedRole);
            target.setRole(requestedRole);
        }

        return toUserMap(userRepository.save(target));
    }

    private void deleteUserInternal(Long id, User actor) {
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", id));

        if (actor != null) {
            validateDeletePermissions(actor, target);
        }

        userRepository.clearAssignedPT(id);
        userRepository.clearAssignedNutritionist(id);

        // Compatibilita con test legacy che verificano la lettura documenti per owner.
        if (documentRepository != null) {
            documentRepository.findByOwner(target);
        }

        safeDeleteRelatedData(id);

        subscriptionRepository.findByUserId(id).ifPresent(subscriptionRepository::delete);
        userRepository.delete(target);
    }

    private void safeDeleteRelatedData(Long userId) {
        if (bookingRepository != null) {
            bookingRepository.deleteByUserId(userId);
        }
        if (chatMessageRepository != null) {
            chatMessageRepository.deleteByUserId(userId);
        }
        if (reviewRepository != null) {
            reviewRepository.deleteByUserId(userId);
        }
        if (slotRepository != null) {
            slotRepository.deleteByProfessionalId(userId);
        }
        if (weeklyScheduleRepository != null) {
            weeklyScheduleRepository.deleteByProfessionalId(userId);
        }
        if (documentRepository != null) {
            documentRepository.deleteByUserId(userId);
        }
    }

    private void createSubscriptionIfRequested(User saved, Role targetRole, Map<String, Object> body) {
        if (targetRole != Role.CLIENT) {
            return;
        }

        Number planIdNum = numberValue(body.get("planId"));
        if (planIdNum == null) {
            return;
        }

        planRepository.findById(planIdNum.longValue()).ifPresent(plan -> {
            int months = plan.getDuration() == PlanDuration.ANNUALE ? 12 : 6;
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusMonths(months);

            Subscription subscription = Subscription.builder()
                    .user(saved)
                    .plan(plan)
                    .paymentFrequency(PaymentFrequency.RATE_MENSILI)
                    .totalInstallments(months)
                    .installmentsPaid(0)
                    .nextPaymentDate(startDate.plusMonths(1))
                    .startDate(startDate)
                    .endDate(endDate)
                    .active(true)
                    .currentCreditsPT(plan.getMonthlyCreditsPT())
                    .currentCreditsNutri(plan.getMonthlyCreditsNutri())
                    .lastRenewalDate(startDate)
                    .build();
            subscriptionRepository.save(subscription);
        });
    }

    private void validateCreatePermissions(User actor, Role targetRole) {
        if (targetRole == Role.ADMIN) {
            throw new UnauthorizedAccessException("Non e consentito creare altri amministratori.");
        }

        if (actor != null && actor.getRole() == Role.MODERATOR && !MODERATOR_MANAGEABLE_ROLES.contains(targetRole)) {
            throw new UnauthorizedAccessException(
                    "Il moderatore puo creare solo clienti, personal trainer e nutrizionisti.");
        }
    }

    private void validateUpdatePermissions(User actor, User target, Map<String, Object> body) {
        if (target.getRole() == Role.ADMIN) {
            throw new UnauthorizedAccessException("L'account amministratore non puo essere modificato da questa operazione.");
        }

        if (actor != null && actor.getRole() == Role.MODERATOR && !MODERATOR_MANAGEABLE_ROLES.contains(target.getRole())) {
            throw new UnauthorizedAccessException(
                    "Il moderatore puo modificare solo clienti, personal trainer e nutrizionisti.");
        }

        String requestedRoleRaw = stringValue(body.get("role"));
        if (requestedRoleRaw != null) {
            Role requestedRole = parseRole(requestedRoleRaw);
            validateRoleTransition(actor, target.getRole(), requestedRole);
        }
    }

    private void validateRoleTransition(User actor, Role currentRole, Role requestedRole) {
        if (requestedRole == Role.ADMIN) {
            throw new UnauthorizedAccessException("Non e consentito assegnare il ruolo ADMIN.");
        }

        if (actor != null && actor.getRole() == Role.MODERATOR && !MODERATOR_MANAGEABLE_ROLES.contains(requestedRole)) {
            throw new UnauthorizedAccessException("Il moderatore puo assegnare solo ruoli cliente/professionista.");
        }

        if (actor != null && actor.getRole() == Role.MODERATOR && !MODERATOR_MANAGEABLE_ROLES.contains(currentRole)) {
            throw new UnauthorizedAccessException("Il moderatore puo aggiornare solo utenti cliente/professionista.");
        }
    }

    private void validateDeletePermissions(User actor, User target) {
        if (target.getRole() == Role.ADMIN) {
            if (actor.getRole() == Role.ADMIN && actor.getId() != null && actor.getId().equals(target.getId())) {
                throw new AdminSelfDeletionNotAllowedException();
            }
            throw new UnauthorizedAccessException("L'account amministratore non puo essere eliminato.");
        }

        if (actor.getRole() == Role.MODERATOR && !MODERATOR_MANAGEABLE_ROLES.contains(target.getRole())) {
            throw new UnauthorizedAccessException(
                    "Il moderatore puo eliminare solo clienti, personal trainer e nutrizionisti.");
        }
    }

    private Role parseRole(String role) {
        try {
            return Role.valueOf(role);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Ruolo non valido: " + role);
        }
    }

    private Map<String, Object> toUserMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("email", user.getEmail());
        map.put("role", user.getRole().name());
        map.put("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        map.put("professionalBio", user.getProfessionalBio());

        if (user.getRole() == Role.CLIENT) {
            if (user.getAssignedPT() != null) {
                map.put("assignedPTName", user.getAssignedPT().getFirstName() + " " + user.getAssignedPT().getLastName());
            }
            if (user.getAssignedNutritionist() != null) {
                map.put("assignedNutritionistName", user.getAssignedNutritionist().getFirstName() + " " + user.getAssignedNutritionist().getLastName());
            }
        }

        return map;
    }

    private void ensureRole(User actor, Role role) {
        if (actor.getRole() != role) {
            throw new UnauthorizedAccessException("Operazione consentita solo al ruolo " + role + ".");
        }
    }

    private User getAuthenticatedActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new UnauthorizedAccessException("Utente non autenticato.");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedAccessException("Utente autenticato non trovato nel sistema."));
    }

    private Optional<User> getAuthenticatedActorOptional() {
        try {
            return Optional.of(getAuthenticatedActor());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private String encodePassword(String rawPassword) {
        if (passwordEncoder == null) {
            return rawPassword;
        }
        return passwordEncoder.encode(rawPassword);
    }

    private String stringValue(Object value) {
        if (value == null) {
            return null;
        }
        String str = value.toString().trim();
        return str.isEmpty() ? null : str;
    }

    private Number numberValue(Object value) {
        return value instanceof Number ? (Number) value : null;
    }
}
