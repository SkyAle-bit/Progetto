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
        User pt = User.builder().id(2L).role(Role.PERSONAL_TRAINER).build();
        User client = User.builder().id(1L).assignedPT(pt).build();

        assertThatCode(() -> strategy.verifyAssignment(client, pt)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("verifyAssignment — fallisce quando PT non è assegnato")
    void verifyAssignment_notAssigned() {
        User pt = User.builder().id(2L).role(Role.PERSONAL_TRAINER).build();
        User client = User.builder().id(1L).assignedPT(null).build();

        assertThatThrownBy(() -> strategy.verifyAssignment(client, pt))
                .isInstanceOf(ProfessionalNotAssignedException.class);
    }

    @Test
    @DisplayName("verifyAssignment — fallisce quando PT diverso da quello assegnato")
    void verifyAssignment_differentPT() {
        User ptAssigned = User.builder().id(3L).role(Role.PERSONAL_TRAINER).build();
        User ptRequested = User.builder().id(2L).role(Role.PERSONAL_TRAINER).build();
        User client = User.builder().id(1L).assignedPT(ptAssigned).build();

        assertThatThrownBy(() -> strategy.verifyAssignment(client, ptRequested))
                .isInstanceOf(ProfessionalNotAssignedException.class);
    }

    @Test
    @DisplayName("consumeCredits — scala un credito PT")
    void consumeCredits_success() {
        Subscription sub = Subscription.builder().currentCreditsPT(5).build();

        strategy.consumeCredits(sub);

        assertThat(sub.getCurrentCreditsPT()).isEqualTo(4);
    }

    @Test
    @DisplayName("consumeCredits — crediti esauriti lancia InsufficientCreditsException")
    void consumeCredits_noCredits() {
        Subscription sub = Subscription.builder().currentCreditsPT(0).build();

        assertThatThrownBy(() -> strategy.consumeCredits(sub))
                .isInstanceOf(InsufficientCreditsException.class);
    }
}

