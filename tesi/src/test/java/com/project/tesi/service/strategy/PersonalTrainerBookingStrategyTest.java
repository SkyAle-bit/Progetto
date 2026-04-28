package com.project.tesi.service.strategy;

import com.project.tesi.enums.Role;
import com.project.tesi.exception.booking.InsufficientCreditsException;
import com.project.tesi.exception.booking.ProfessionalNotAssignedException;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Test unitari per {@link PersonalTrainerBookingStrategy}.
 */
class PersonalTrainerBookingStrategyTest {

    private final PersonalTrainerBookingStrategy strategy = new PersonalTrainerBookingStrategy();

    @Test
    @DisplayName("getSupportedRole — restituisce PERSONAL_TRAINER")
    void getSupportedRole() {
        assertThat(strategy.getSupportedRole()).isEqualTo(Role.PERSONAL_TRAINER);
    }

    @Test
    @DisplayName("verifyAssignment — successo quando PT è assegnato correttamente")
    void verifyAssignment_success() {
        User pt = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(2L).email("x@x.com").password("a").role(Role.PERSONAL_TRAINER).build();
        User client = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(1L).email("y@y.com").password("b").role(Role.CLIENT).assignedPT(pt).build();

        assertThatCode(() -> strategy.verifyAssignment(client, pt)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("verifyAssignment — fallisce quando PT non è assegnato")
    void verifyAssignment_notAssigned() {
        User pt = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(2L).email("x@x.com").password("a").role(Role.PERSONAL_TRAINER).build();
        User client = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(1L).email("y@y.com").password("b").role(Role.CLIENT).assignedPT(null).build();

        assertThatThrownBy(() -> strategy.verifyAssignment(client, pt))
                .isInstanceOf(ProfessionalNotAssignedException.class);
    }

    @Test
    @DisplayName("verifyAssignment — fallisce quando PT diverso da quello assegnato")
    void verifyAssignment_differentPT() {
        User ptAssigned = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(3L).email("z@z.com").password("a").role(Role.PERSONAL_TRAINER).build();
        User ptRequested = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(2L).email("x@x.com").password("b").role(Role.PERSONAL_TRAINER).build();
        User client = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(1L).email("c@c.com").password("c").role(Role.CLIENT).assignedPT(ptAssigned).build();

        assertThatThrownBy(() -> strategy.verifyAssignment(client, ptRequested))
                .isInstanceOf(ProfessionalNotAssignedException.class);
    }

    @Test
    @DisplayName("consumeCredits — scala un credito PT")
    void consumeCredits_success() {
        com.project.tesi.model.Plan plan = new com.project.tesi.model.Plan();
        com.project.tesi.model.User user = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).email("x@x.com").password("x").role(Role.CLIENT).build();
        Subscription sub = Subscription.builder().user(new com.project.tesi.model.User()).plan(new com.project.tesi.model.Plan()).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).user(user).plan(plan).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).currentCreditsPT(5).build();

        strategy.consumeCredits(sub);

        assertThat(sub.getCurrentCreditsPT()).isEqualTo(4);
    }

    @Test
    @DisplayName("consumeCredits — crediti esauriti lancia InsufficientCreditsException")
    void consumeCredits_noCredits() {
        com.project.tesi.model.Plan plan = new com.project.tesi.model.Plan();
        com.project.tesi.model.User user = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).email("x@x.com").password("x").role(Role.CLIENT).build();
        Subscription sub = Subscription.builder().user(new com.project.tesi.model.User()).plan(new com.project.tesi.model.Plan()).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).user(user).plan(plan).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).currentCreditsPT(0).build();

        assertThatThrownBy(() -> strategy.consumeCredits(sub))
                .isInstanceOf(InsufficientCreditsException.class);
    }
}

