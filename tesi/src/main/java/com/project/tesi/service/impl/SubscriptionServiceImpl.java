package com.project.tesi.service.impl;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.response.SubscriptionResponse;
import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.enums.PlanDuration;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.exception.subscription.SubscriptionNotFoundException;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.PlanRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.SubscriptionService;
import com.project.tesi.service.strategy.BookingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Gestisce il ciclo di vita degli abbonamenti.
 *
 * L'abbonamento è essenzialmente un'istanza attiva di un Piano per un utente. 
 * Quando si attiva un nuovo abbonamento, disattiviamo il precedente (niente sovrapposizioni).
 * I crediti PT/Nutrizionista vengono riempiti basandosi sul Piano scelto.
 *
 * NOTA: usiamo questa classe come punto di ingresso centrale per scalare/rimborsare
 * i crediti dagli Observer. In questo modo evitiamo che i listener facciano chiamate
 * dirette al database, rispettando i confini architetturali.
 */
@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final List<BookingStrategy> strategies;

    // Costruttore esplicito — sostituisce @RequiredArgsConstructor di Lombok
    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   PlanRepository planRepository,
                                   UserRepository userRepository,
                                   List<BookingStrategy> strategies) {
        this.subscriptionRepository = subscriptionRepository;
        this.planRepository = planRepository;
        this.userRepository = userRepository;
        this.strategies = strategies;
    }

    @Override
    @Transactional
    public SubscriptionResponse activateSubscription(PlanRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utente", request.getUserId()));

        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Piano", request.getPlanId()));

        // Se esiste già un abbonamento attivo, lo disattiviamo in modo "soft".
        // Scegliamo di non estenderlo, ma di piallare i vecchi crediti e iniziare un nuovo ciclo.
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

    // Scala i crediti quando scatta l'evento di avvenuta prenotazione (via Observer).
    // Delega il "come" scalare alla Strategy corretta (PT vs Nutri).
    @Override
    @Transactional
    public void deductCredits(Booking booking) {
        User user = booking.getUser();
        User professional = booking.getProfessional();

        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(user)
                .orElseThrow(() -> new IllegalStateException(
                        "Abbonamento non trovato per l'utente " + user.getId()));

        BookingStrategy strategy = strategies.stream()
                .filter(s -> s.getSupportedRole() == professional.getRole())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Nessuna strategy trovata per il ruolo: " + professional.getRole()));

        strategy.consumeCredits(sub);
        subscriptionRepository.save(sub);
    }

    // Restituisce il credito perso se si annulla la prenotazione (sempre chiamato via Observer).
    @Override
    @Transactional
    public void refundCredits(Booking booking) {
        User professional = booking.getProfessional();

        BookingStrategy strategy = strategies.stream()
                .filter(s -> s.getSupportedRole() == professional.getRole())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Nessuna strategy trovata per il ruolo: " + professional.getRole()));

        Subscription sub = subscriptionRepository.findByUserAndActiveTrue(booking.getUser())
                .orElseThrow(() -> new IllegalStateException(
                        "Nessun abbonamento attivo trovato per l'utente"));

        strategy.refundCredits(sub);
        subscriptionRepository.save(sub);
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