package com.project.tesi.controller;

import com.project.tesi.enums.Role;
import com.project.tesi.enums.PlanDuration;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DocumentRepository documentRepository;
    private final PasswordEncoder passwordEncoder;

    // ── GET ALL USERS ──────────────────────────────────────────
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("firstName", u.getFirstName());
            map.put("lastName", u.getLastName());
            map.put("email", u.getEmail());
            map.put("role", u.getRole().name());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    // ── GET ALL SUBSCRIPTIONS ──────────────────────────────────
    @GetMapping("/subscriptions")
    public ResponseEntity<List<Map<String, Object>>> getAllSubscriptions() {
        List<Subscription> subs = subscriptionRepository.findAll();
        List<Map<String, Object>> result = subs.stream().map(s -> {
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
        return ResponseEntity.ok(result);
    }

    // ── CREATE USER ────────────────────────────────────────────
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> body) {
        String email = (String) body.get("email");
        String firstName = (String) body.get("firstName");
        String lastName = (String) body.get("lastName");
        String password = (String) body.get("password");
        String role = (String) body.get("role");

        if (email == null || firstName == null || lastName == null || password == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Tutti i campi sono obbligatori"));
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email già in uso"));
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
                Optional<Plan> planOpt = planRepository.findById(planIdNum.longValue());
                if (planOpt.isPresent()) {
                    Plan plan = planOpt.get();
                    int months = plan.getDuration() == PlanDuration.ANNUALE ? 12 : 6;
                    LocalDate startDate = LocalDate.now();
                    LocalDate endDate = startDate.plusMonths(months);

                    Subscription subscription = Subscription.builder()
                            .user(saved)
                            .plan(plan)
                            .paymentFrequency(com.project.tesi.enums.PaymentFrequency.RATE_MENSILI)
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
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", saved.getId());
        result.put("firstName", saved.getFirstName());
        result.put("lastName", saved.getLastName());
        result.put("email", saved.getEmail());
        result.put("role", saved.getRole().name());
        return ResponseEntity.ok(result);
    }

    // ── DELETE USER ────────────────────────────────────────────
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = opt.get();

        // Elimina documenti associati
        documentRepository.findByOwner(user).forEach(documentRepository::delete);
        // Elimina sottoscrizione se presente
        subscriptionRepository.findByUserId(id).ifPresent(subscriptionRepository::delete);
        // Elimina utente
        userRepository.delete(user);
        return ResponseEntity.ok(Map.of("message", "Utente eliminato"));
    }

    // ── CREATE PLAN ────────────────────────────────────────────
    @PostMapping("/plans")
    public ResponseEntity<?> createPlan(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        String duration = (String) body.get("duration");
        Double fullPrice = body.get("fullPrice") != null ? ((Number) body.get("fullPrice")).doubleValue() : null;
        Double monthlyPrice = body.get("monthlyInstallmentPrice") != null ? ((Number) body.get("monthlyInstallmentPrice")).doubleValue() : null;
        Integer ptCredits = body.get("monthlyCreditsPT") != null ? ((Number) body.get("monthlyCreditsPT")).intValue() : null;
        Integer nutriCredits = body.get("monthlyCreditsNutri") != null ? ((Number) body.get("monthlyCreditsNutri")).intValue() : null;

        if (name == null || duration == null || fullPrice == null || monthlyPrice == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Campi obbligatori mancanti"));
        }
        if (planRepository.findByName(name).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Piano con questo nome già esistente"));
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
        return ResponseEntity.ok(saved);
    }

    // ── DELETE PLAN ────────────────────────────────────────────
    @DeleteMapping("/plans/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable Long id) {
        Optional<Plan> opt = planRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Controlla se qualche sottoscrizione usa questo piano
        boolean hasSubscribers = subscriptionRepository.findAll().stream()
                .anyMatch(s -> s.getPlan() != null && s.getPlan().getId().equals(id));
        if (hasSubscribers) {
            return ResponseEntity.badRequest().body(Map.of("error", "Impossibile eliminare: ci sono utenti con questo piano"));
        }
        planRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Piano eliminato"));
    }
}
