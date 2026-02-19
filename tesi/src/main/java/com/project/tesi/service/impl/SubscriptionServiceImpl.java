package com.project.tesi.service.impl;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.enums.PlanDuration;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.PlanRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubscriptionResponse activateSubscription(PlanRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Piano non trovato"));

        // Calcolo date
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = (request.getDuration() == PlanDuration.ANNUALE)
                ? startDate.plusYears(1)
                : startDate.plusMonths(6);

        // ⚠️ LOGICA CORRETTA:
        // Cerca un abbonamento esistente. Se esiste lo riutilizza (UPDATE),
        // se non esiste ne istanzia uno nuovo vuoto (INSERT).
        Subscription sub = subscriptionRepository.findByUserAndIsActiveTrue(user)
                .orElse(new Subscription());

        // Aggiorniamo tutti i campi dell'oggetto
        sub.setUser(user);
        sub.setPlan(plan);
        sub.setStartDate(startDate);
        sub.setEndDate(endDate);
        sub.setActive(true);
        sub.setPaymentFrequency(request.getPaymentFrequency());

        // Assegna i nuovi crediti previsti dal piano
        sub.setCurrentCreditsPT(plan.getMonthlyCreditsPT());
        sub.setCurrentCreditsNutri(plan.getMonthlyCreditsNutri());

        // Save ora farà automaticamente un UPDATE se l'oggetto esisteva già
        Subscription savedSub = subscriptionRepository.save(sub);

        return mapToResponse(savedSub);
    }

    @Override
    public SubscriptionResponse getSubscriptionStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        Subscription sub = subscriptionRepository.findByUserAndIsActiveTrue(user)
                .orElseThrow(() -> new RuntimeException("Nessun abbonamento attivo trovato"));

        return mapToResponse(sub);
    }

    private SubscriptionResponse mapToResponse(Subscription sub) {
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .planName(sub.getPlan().getName())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .isActive(sub.isActive())
                .remainingPtCredits(sub.getCurrentCreditsPT())
                .remainingNutritionistCredits(sub.getCurrentCreditsNutri())
                .build();
    }
}