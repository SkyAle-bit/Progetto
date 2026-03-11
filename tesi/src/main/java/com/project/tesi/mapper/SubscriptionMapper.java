package com.project.tesi.mapper;

import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Mapper per la creazione dell'entità {@link Subscription} a partire dai dati
 * di registrazione ({@link RegisterRequest}), l'utente e il piano scelto.
 *
 * Calcola automaticamente:
 * <ul>
 *   <li>Data di inizio (oggi) e data di fine (oggi + durata del piano)</li>
 *   <li>Numero di rate totali in base alla frequenza di pagamento</li>
 *   <li>Data del prossimo pagamento (null se pagamento in unica soluzione)</li>
 *   <li>Crediti iniziali PT e Nutrizionista dal piano</li>
 * </ul>
 */
@Component
public class SubscriptionMapper {

    /**
     * Crea una nuova entità Subscription a partire dai dati di registrazione.
     *
     * @param request dati della registrazione (contiene la frequenza di pagamento)
     * @param user    l'utente a cui associare l'abbonamento
     * @param plan    il piano commerciale scelto
     * @return l'entità Subscription pronta per il salvataggio, oppure {@code null} se un parametro è null
     */
    public Subscription toSubscription(RegisterRequest request, User user, Plan plan) {
        if (request == null || user == null || plan == null) {
            return null;
        }

        LocalDate startDate = LocalDate.now();
        int months = plan.getDuration().getMonths();
        LocalDate endDate = startDate.plusMonths(months);

        // Calcolo rate: unica soluzione = 1 rata, rate mensili = numero di mesi
        int totalInstallments = request.getPaymentFrequency() == PaymentFrequency.UNICA_SOLUZIONE ? 1 : months;

        return Subscription.builder()
                .user(user)
                .plan(plan)
                .paymentFrequency(request.getPaymentFrequency())
                .installmentsPaid(1) // Prima rata pagata subito alla sottoscrizione
                .totalInstallments(totalInstallments)
                .nextPaymentDate(request.getPaymentFrequency() == PaymentFrequency.UNICA_SOLUZIONE ? null : startDate.plusMonths(1))
                .startDate(startDate)
                .endDate(endDate)
                .active(true)
                .currentCreditsPT(plan.getMonthlyCreditsPT())
                .currentCreditsNutri(plan.getMonthlyCreditsNutri())
                .lastRenewalDate(startDate)
                .build();
    }
}