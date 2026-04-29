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
 * Implementazione del servizio per la gestione degli abbonamenti.
 *
 * Gestisce:
 * <ul>
 *   <li>Attivazione di un nuovo piano (disattivando il precedente se presente)</li>
 *   <li>Calcolo delle date (inizio/fine) e delle rate in base al piano e alla frequenza</li>
 *   <li>Inizializzazione dei crediti PT e Nutrizionista</li>
 *   <li>Consultazione dello stato dell'abbonamento attivo</li>
 *   <li>Deduzione e rimborso dei crediti per conto dei listener Observer,
 *       evitando che i listener accedano direttamente al repository (violazione di layer)</li>
 * </ul>
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

    /**
     * Scala i crediti dell'abbonamento dell'utente in base al ruolo del professionista.
     *
     * <p>Seleziona la {@link BookingStrategy} appropriata tramite il ruolo del professionista
     * e ne invoca {@code consumeCredits()}, poi persiste l'abbonamento aggiornato.
     * Questo metodo è il punto di delegazione per il {@code CreditDeductionListener},
     * garantendo che la logica di business rimanga nel service layer.</p>
     *
     * @param booking la prenotazione per cui scalare i crediti
     */
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

    /**
     * Rimborsa i crediti dell'abbonamento dell'utente in base al ruolo del professionista.
     *
     * <p>Seleziona la {@link BookingStrategy} appropriata tramite il ruolo del professionista
     * e ne invoca {@code refundCredits()}, poi persiste l'abbonamento aggiornato.
     * Questo metodo è il punto di delegazione per il {@code CreditRefundListener},
     * garantendo che la logica di business rimanga nel service layer.</p>
     *
     * @param booking la prenotazione annullata per cui rimborsare i crediti
     */
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