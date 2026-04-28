package com.project.tesi.mapper;

import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.enums.PlanDuration;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitari per {@link SubscriptionMapper}.
 */
class SubscriptionMapperTest {

    private final SubscriptionMapper mapper = new SubscriptionMapper();

    @Test
    @DisplayName("toSubscription — unica soluzione annuale")
    void toSubscription_unicaSoluzione() {
        User user = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(1L).build();
        Plan plan = Plan.builder().name("plan").duration(com.project.tesi.enums.PlanDuration.ANNUALE).fullPrice(100.0).monthlyInstallmentPrice(10.0).id(1L).duration(PlanDuration.ANNUALE)
                .monthlyCreditsPT(8).monthlyCreditsNutri(4).build();
        RegisterRequest req = new RegisterRequest();
        req.setPaymentFrequency(PaymentFrequency.UNICA_SOLUZIONE);

        Subscription sub = mapper.toSubscription(req, user, plan);

        assertThat(sub).isNotNull();
        assertThat(sub.getUser()).isEqualTo(user);
        assertThat(sub.getPlan()).isEqualTo(plan);
        assertThat(sub.isActive()).isTrue();
        assertThat(sub.getInstallmentsPaid()).isEqualTo(1);
        assertThat(sub.getTotalInstallments()).isEqualTo(1);
        assertThat(sub.getNextPaymentDate()).isNull();
        assertThat(sub.getCurrentCreditsPT()).isEqualTo(8);
        assertThat(sub.getCurrentCreditsNutri()).isEqualTo(4);
    }

    @Test
    @DisplayName("toSubscription — rate mensili semestrali")
    void toSubscription_rateMensili() {
        User user = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(1L).build();
        Plan plan = Plan.builder().name("plan").duration(com.project.tesi.enums.PlanDuration.ANNUALE).fullPrice(100.0).monthlyInstallmentPrice(10.0).id(2L).duration(PlanDuration.SEMESTRALE)
                .monthlyCreditsPT(4).monthlyCreditsNutri(2).build();
        RegisterRequest req = new RegisterRequest();
        req.setPaymentFrequency(PaymentFrequency.RATE_MENSILI);

        Subscription sub = mapper.toSubscription(req, user, plan);

        assertThat(sub).isNotNull();
        assertThat(sub.getTotalInstallments()).isEqualTo(6);
        assertThat(sub.getNextPaymentDate()).isNotNull();
    }

    @Test
    @DisplayName("toSubscription — null input restituisce null")
    void toSubscription_nullInputs() {
        assertThat(mapper.toSubscription(null, User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).build(), Plan.builder().name("plan").duration(com.project.tesi.enums.PlanDuration.ANNUALE).fullPrice(100.0).monthlyInstallmentPrice(10.0).build())).isNull();
        assertThat(mapper.toSubscription(new RegisterRequest(), null, Plan.builder().name("plan").duration(com.project.tesi.enums.PlanDuration.ANNUALE).fullPrice(100.0).monthlyInstallmentPrice(10.0).build())).isNull();
        assertThat(mapper.toSubscription(new RegisterRequest(), User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).build(), null)).isNull();
    }
}

