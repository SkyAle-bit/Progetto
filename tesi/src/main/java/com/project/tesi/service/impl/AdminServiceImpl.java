package com.project.tesi.service.impl;

import com.project.tesi.enums.PlanDuration;
import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.common.ResourceAlreadyExistsException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.*;
import com.project.tesi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio di amministrazione.
 *
 * Gestisce le operazioni CRUD su utenti, piani e abbonamenti:
 * <ul>
 *   <li>Creazione utenti (con assegnazione professionisti e piano)</li>
 *   <li>Eliminazione utenti (con pulizia documenti e abbonamenti)</li>
 *   <li>Creazione/eliminazione piani commerciali</li>
 *   <li>Visualizzazione abbonamenti attivi e scaduti</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("firstName", u.getFirstName());
            map.put("lastName", u.getLastName());
            map.put("email", u.getEmail());
            map.put("role", u.getRole().name());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> createUser(Map<String, Object> body) {
        String email = (String) body.get("email");
        String firstName = (String) body.get("firstName");
        String lastName = (String) body.get("lastName");
        String password = (String) body.get("password");
        String role = (String) body.get("role");

        if (email == null || firstName == null || lastName == null || password == null || role == null) {
            throw new IllegalArgumentException("Tutti i campi sono obbligatori (email, firstName, lastName, password, role).");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResourceAlreadyExistsException("Utente", "email", email);
        }

        User user = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(passwordEncoder.encode(password))
                .role(Role.valueOf(role))
                .build();

        // Se è un CLIENT, assegna professionisti
        if (Role.valueOf(role) == Role.CLIENT) {
            Number ptIdNum = (Number) body.get("assignedPTId");
            Number nutriIdNum = (Number) body.get("assignedNutritionistId");

            if (ptIdNum != null) {
                userRepository.findById(ptIdNum.longValue()).ifPresent(user::setAssignedPT);
            }
            if (nutriIdNum != null) {
                userRepository.findById(nutriIdNum.longValue()).ifPresent(user::setAssignedNutritionist);
            }
        }

        User saved = userRepository.save(user);

        // Se è un CLIENT e ha un piano, crea la sottoscrizione
        if (Role.valueOf(role) == Role.CLIENT) {
            Number planIdNum = (Number) body.get("planId");
            if (planIdNum != null) {
                planRepository.findById(planIdNum.longValue()).ifPresent(plan -> {
                    int months = plan.getDuration() == PlanDuration.ANNUALE ? 12 : 6;
                    LocalDate startDate = LocalDate.now();
                    LocalDate endDate = startDate.plusMonths(months);

                    Subscription subscription = Subscription.builder()
                            .user(saved)
                            .plan(plan)
                            .paymentFrequency(PaymentFrequency.RATE_MENSILI)
                            .installmentsPaid(0)
                            .totalInstallments(months)
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
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId());
        result.put("firstName", saved.getFirstName());
        result.put("lastName", saved.getLastName());
        result.put("email", saved.getEmail());
        result.put("role", saved.getRole().name());
        return result;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", id));

        documentRepository.findByOwner(user).forEach(documentRepository::delete);
        subscriptionRepository.findByUserId(id).ifPresent(subscriptionRepository::delete);
        userRepository.delete(user);
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
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Map<String, Object> createPlan(Map<String, Object> body) {
        String name = (String) body.get("name");
        String duration = (String) body.get("duration");
        Double fullPrice = body.get("fullPrice") != null ? ((Number) body.get("fullPrice")).doubleValue() : null;
        Double monthlyPrice = body.get("monthlyInstallmentPrice") != null ? ((Number) body.get("monthlyInstallmentPrice")).doubleValue() : null;
        Integer ptCredits = body.get("monthlyCreditsPT") != null ? ((Number) body.get("monthlyCreditsPT")).intValue() : null;
        Integer nutriCredits = body.get("monthlyCreditsNutri") != null ? ((Number) body.get("monthlyCreditsNutri")).intValue() : null;

        if (name == null || duration == null || fullPrice == null || monthlyPrice == null) {
            throw new IllegalArgumentException("Campi obbligatori mancanti (name, duration, fullPrice, monthlyInstallmentPrice).");
        }
        if (planRepository.findByName(name).isPresent()) {
            throw new ResourceAlreadyExistsException("Piano", "nome", name);
        }

        Plan plan = Plan.builder()
                .name(name)
                .duration(PlanDuration.valueOf(duration))
                .fullPrice(fullPrice)
                .monthlyInstallmentPrice(monthlyPrice)
                .monthlyCreditsPT(ptCredits != null ? ptCredits : 0)
                .monthlyCreditsNutri(nutriCredits != null ? nutriCredits : 0)
                .insuranceCoverageDetails("Copertura inclusa")
                .build();
        Plan saved = planRepository.save(plan);

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId());
        result.put("name", saved.getName());
        result.put("duration", saved.getDuration().name());
        result.put("fullPrice", saved.getFullPrice());
        result.put("monthlyInstallmentPrice", saved.getMonthlyInstallmentPrice());
        return result;
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piano", id));

        boolean hasSubscribers = subscriptionRepository.findAll().stream()
                .anyMatch(s -> s.getPlan() != null && s.getPlan().getId().equals(id));
        if (hasSubscribers) {
            throw new IllegalStateException("Impossibile eliminare: ci sono utenti con questo piano.");
        }
        planRepository.delete(plan);
    }
}

