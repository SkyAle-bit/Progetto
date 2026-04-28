package com.project.tesi.scheduler;

import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.model.Subscription;
import com.project.tesi.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(SubscriptionScheduler.class);
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
                if (sub.getPaymentFrequency() == PaymentFrequency.RATE_MENSILI) {
                    if (sub.getNextPaymentDate() != null 
                            && !today.isBefore(sub.getNextPaymentDate()) 
                            && sub.getInstallmentsPaid() < sub.getTotalInstallments()) {
                        
                        sub.setInstallmentsPaid(sub.getInstallmentsPaid() + 1);
                        sub.setNextPaymentDate(sub.getNextPaymentDate().plusMonths(1));
                    } else {
                        log.warn("Pagamento rateale non dovuto per l'abbonamento ID {}: salto il reset dei crediti", sub.getId());
                        continue;
                    }
                }

                sub.setCurrentCreditsPT(sub.getPlan().getMonthlyCreditsPT());
                sub.setCurrentCreditsNutri(sub.getPlan().getMonthlyCreditsNutri());
                sub.setLastRenewalDate(today);

                subscriptionRepository.save(sub);
            }
        }
    }
}