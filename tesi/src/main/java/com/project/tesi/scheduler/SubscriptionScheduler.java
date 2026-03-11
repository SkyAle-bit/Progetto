package com.project.tesi.scheduler;

import com.project.tesi.model.Subscription;
import com.project.tesi.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler per il rinnovo automatico dei crediti mensili degli abbonamenti.
 *
 * Viene eseguito ogni notte a mezzanotte (cron: {@code 0 0 0 * * ?}).
 * Al primo giorno di ogni mese, resetta i crediti PT e Nutrizionista
 * di tutti gli abbonamenti attivi secondo i valori previsti dal piano sottoscritto.
 */
@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;

    /**
     * Rinnova i crediti mensili per tutti gli abbonamenti attivi.
     * Eseguito automaticamente ogni notte a mezzanotte.
     * Il reset avviene solo il primo giorno del mese (logica semplificata).
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void renewCredits() {
        List<Subscription> activeSubs = subscriptionRepository.findByActiveTrue();
        LocalDate today = LocalDate.now();

        for (Subscription sub : activeSubs) {
            // Reset crediti il primo giorno di ogni mese
            if (today.getDayOfMonth() == 1) {
                sub.setCurrentCreditsPT(sub.getPlan().getMonthlyCreditsPT());
                sub.setCurrentCreditsNutri(sub.getPlan().getMonthlyCreditsNutri());
                sub.setLastRenewalDate(today);

                subscriptionRepository.save(sub);
            }
        }
    }
}