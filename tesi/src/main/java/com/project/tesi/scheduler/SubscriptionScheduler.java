package com.project.tesi.scheduler;

import com.project.tesi.model.Subscription;
import com.project.tesi.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionRepository subscriptionRepository;

    // Esegue ogni notte alle 00:00
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void renewCredits() {
        List<Subscription> activeSubs = subscriptionRepository.findByActiveTrue();
        LocalDate today = LocalDate.now();

        for (Subscription sub : activeSubs) {
            // Se è passato un mese dall'ultimo rinnovo
            if (today.getDayOfMonth() == 1) { // Esempio semplificato: reset al primo del mese
                // Reset Crediti in base al Piano Originale
                sub.setCurrentCreditsPT(sub.getPlan().getMonthlyCreditsPT());
                sub.setCurrentCreditsNutri(sub.getPlan().getMonthlyCreditsNutri());
                sub.setLastRenewalDate(today);

                subscriptionRepository.save(sub);
            }
        }
    }
}