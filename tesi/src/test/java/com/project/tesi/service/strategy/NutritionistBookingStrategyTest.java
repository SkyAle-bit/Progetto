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
 * Test unitari per {@link NutritionistBookingStrategy}.
 */
class NutritionistBookingStrategyTest {

    private final NutritionistBookingStrategy strategy = new NutritionistBookingStrategy();

    @Test
    @DisplayName("getSupportedRole — restituisce NUTRITIONIST")
    void getSupportedRole() {
        assertThat(strategy.getSupportedRole()).isEqualTo(Role.NUTRITIONIST);
    }

    @Test
    @DisplayName("verifyAssignment — successo quando nutrizionista è assegnato correttamente")
    void verifyAssignment_success() {
        User nutri = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(3L).email("x@x.com").password("a").role(Role.NUTRITIONIST).build();
        User client = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(1L).email("y@y.com").password("b").role(Role.CLIENT).assignedNutritionist(nutri).build();

        assertThatCode(() -> strategy.verifyAssignment(client, nutri)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("verifyAssignment — fallisce quando nutrizionista non è assegnato")
    void verifyAssignment_notAssigned() {
        User nutri = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(3L).email("x@x.com").password("a").role(Role.NUTRITIONIST).build();
        User client = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(1L).email("y@y.com").password("b").role(Role.CLIENT).assignedNutritionist(null).build();

        assertThatThrownBy(() -> strategy.verifyAssignment(client, nutri))
                .isInstanceOf(ProfessionalNotAssignedException.class);
    }

    @Test
    @DisplayName("verifyAssignment — fallisce quando nutrizionista diverso da quello assegnato")
    void verifyAssignment_differentNutri() {
        User nutriAssigned = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(4L).email("z@z.com").password("a").role(Role.NUTRITIONIST).build();
        User nutriRequested = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(3L).email("x@x.com").password("b").role(Role.NUTRITIONIST).build();
        User client = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).id(1L).email("c@c.com").password("c").role(Role.CLIENT).assignedNutritionist(nutriAssigned).build();

        assertThatThrownBy(() -> strategy.verifyAssignment(client, nutriRequested))
                .isInstanceOf(ProfessionalNotAssignedException.class);
    }

    @Test
    @DisplayName("consumeCredits — scala un credito Nutrizionista")
    void consumeCredits_success() {
        com.project.tesi.model.Plan plan = new com.project.tesi.model.Plan();
        com.project.tesi.model.User user = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).email("x@x.com").password("x").role(Role.CLIENT).build();
        Subscription sub = Subscription.builder().user(new com.project.tesi.model.User()).plan(new com.project.tesi.model.Plan()).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).user(user).plan(plan).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).currentCreditsNutri(3).build();

        strategy.consumeCredits(sub);

        assertThat(sub.getCurrentCreditsNutri()).isEqualTo(2);
    }

    @Test
    @DisplayName("consumeCredits — crediti esauriti lancia InsufficientCreditsException")
    void consumeCredits_noCredits() {
        com.project.tesi.model.Plan plan = new com.project.tesi.model.Plan();
        com.project.tesi.model.User user = User.builder().email("test@test.com").password("pass").role(com.project.tesi.enums.Role.CLIENT).email("x@x.com").password("x").role(Role.CLIENT).build();
        Subscription sub = Subscription.builder().user(new com.project.tesi.model.User()).plan(new com.project.tesi.model.Plan()).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).user(user).plan(plan).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE).currentCreditsNutri(0).build();

        assertThatThrownBy(() -> strategy.consumeCredits(sub))
                .isInstanceOf(InsufficientCreditsException.class);
    }
}

