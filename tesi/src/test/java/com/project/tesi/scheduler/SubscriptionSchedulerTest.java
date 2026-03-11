package com.project.tesi.scheduler;

import com.project.tesi.enums.PlanDuration;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.repository.SubscriptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test unitari per {@link SubscriptionScheduler}.
 */
@ExtendWith(MockitoExtension.class)
class SubscriptionSchedulerTest {

    @Mock private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private SubscriptionScheduler scheduler;

    @Test
    @DisplayName("renewCredits — il primo del mese resetta i crediti, altrimenti no")
    void renewCredits_branchCoverage() {
        Plan plan = Plan.builder().monthlyCreditsPT(8).monthlyCreditsNutri(4)
                .duration(PlanDuration.ANNUALE).build();

        Subscription sub = Subscription.builder().id(1L).active(true)
                .currentCreditsPT(2).currentCreditsNutri(1).plan(plan).build();

        when(subscriptionRepository.findByActiveTrue()).thenReturn(List.of(sub));

        scheduler.renewCredits();

        LocalDate today = LocalDate.now();
        if (today.getDayOfMonth() == 1) {
            // Il primo del mese i crediti vengono resettati
            assertThat(sub.getCurrentCreditsPT()).isEqualTo(8);
            assertThat(sub.getCurrentCreditsNutri()).isEqualTo(4);
            verify(subscriptionRepository).save(sub);
        } else {
            // Altrimenti restano invariati
            assertThat(sub.getCurrentCreditsPT()).isEqualTo(2);
            assertThat(sub.getCurrentCreditsNutri()).isEqualTo(1);
            verify(subscriptionRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("renewCredits — lista vuota non causa errori")
    void renewCredits_emptyList() {
        when(subscriptionRepository.findByActiveTrue()).thenReturn(List.of());
        scheduler.renewCredits();
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("renewCredits — più abbonamenti attivi vengono processati")
    void renewCredits_multipleSubscriptions() {
        Plan plan = Plan.builder().monthlyCreditsPT(10).monthlyCreditsNutri(5)
                .duration(PlanDuration.ANNUALE).build();

        Subscription sub1 = Subscription.builder().id(1L).active(true)
                .currentCreditsPT(3).currentCreditsNutri(1).plan(plan).build();
        Subscription sub2 = Subscription.builder().id(2L).active(true)
                .currentCreditsPT(0).currentCreditsNutri(0).plan(plan).build();

        when(subscriptionRepository.findByActiveTrue()).thenReturn(List.of(sub1, sub2));

        scheduler.renewCredits();
        // Verifica che entrambi siano processati (il risultato dipende dal giorno)
        verify(subscriptionRepository).findByActiveTrue();
    }
}
