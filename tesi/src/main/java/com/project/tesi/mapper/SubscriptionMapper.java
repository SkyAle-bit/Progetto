package com.project.tesi.mapper;

import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SubscriptionMapper {

    public Subscription toSubscription(RegisterRequest request, User user, Plan plan) {
        if (request == null || user == null || plan == null) {
            return null;
        }

        LocalDate startDate = LocalDate.now();
        int months = plan.getDuration().getMonths();
        LocalDate endDate = startDate.plusMonths(months);

        int totalInstallments = request.getPaymentFrequency() == PaymentFrequency.UNICA_SOLUZIONE ? 1 : months;
        return Subscription.builder()
                .user(user)
                .plan(plan)
                .paymentFrequency(request.getPaymentFrequency())
                .installmentsPaid(request.getPaymentFrequency() == PaymentFrequency.UNICA_SOLUZIONE ? 1 : 1) // prima rata pagata subito
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