package com.project.tesi.service.impl;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.enums.PlanDuration;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.exception.subscription.SubscriptionNotFoundException;
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
import java.util.Optional;

/**
 * Implementazione del servizio per la gestione degli abbonamenti.
 *
 * Gestisce:
 * <ul>
 *   <li>Attivazione di un nuovo piano (disattivando il precedente se presente)</li>
 *   <li>Calcolo delle date (inizio/fine) e delle rate in base al piano e alla frequenza</li>
 *   <li>Inizializzazione dei crediti PT e Nutrizionista</li>
 *   <li>Consultazione dello stato dell'abbonamento attivo</li>
 * </ul>
 */
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
                .orElseThrow(() -> new ResourceNotFoundException("Utente", request.getUserId()));

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Piano", request.getPlanId()));

        // Se esiste già un abbonamento attivo, lo disattiviamo (o lanciamo eccezione)
        // Qui scegliamo di disattivare il precedente e crearne uno nuovo
        Optional<Subscription> existingActive = subscriptionRepository.findByUserAndActiveTrue(user);
        existingActive.ifPresent(sub -> {
            sub.setActive(false);
            subscriptionRepository.save(sub);
        });

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = plan.getDuration() == PlanDuration.ANNUALE
                ? startDate.plusYears(1)
                : startDate.plusMonths(6);

        Subscription sub = Subscription.builder()
                .user(user)
                .plan(plan)
                .paymentFrequency(request.getPaymentFrequency())
                .startDate(startDate)
                .endDate(endDate)
                .active(true)
                .currentCreditsPT(plan.getMonthlyCreditsPT())
                .currentCreditsNutri(plan.getMonthlyCreditsNutri())
                .lastRenewalDate(startDate)
                .build();

        // Gestione pagamento iniziale
        if (request.getPaymentFrequency() == PaymentFrequency.UNICA_SOLUZIONE) {
            sub.setInstallmentsPaid(1);
            sub.setTotalInstallments(1);
            sub.setNextPaymentDate(null);
        } else {
            sub.setInstallmentsPaid(1); // prima rata pagata
            sub.setTotalInstallments(plan.getDuration().getMonths());
            sub.setNextPaymentDate(startDate.plusMonths(1));
        }

        Subscription saved = subscriptionRepository.save(sub);
        return mapToResponse(saved);
    }

    @Override
    public SubscriptionResponse getSubscriptionStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", userId));

        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(user)
                .orElseThrow(SubscriptionNotFoundException::new);

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