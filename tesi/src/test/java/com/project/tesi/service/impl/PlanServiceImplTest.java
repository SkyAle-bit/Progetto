package com.project.tesi.service.impl;

import com.project.tesi.enums.PlanDuration;
import com.project.tesi.model.Plan;
import com.project.tesi.repository.PlanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test unitari per {@link PlanServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class PlanServiceImplTest {

    @Mock private PlanRepository planRepository;

    @InjectMocks
    private PlanServiceImpl planService;

    @Test
    @DisplayName("getAllPlans — restituisce tutti i piani")
    void getAllPlans() {
        Plan p1 = Plan.builder().id(1L).name("Base").duration(PlanDuration.SEMESTRALE).build();
        Plan p2 = Plan.builder().id(2L).name("Premium").duration(PlanDuration.ANNUALE).build();
        when(planRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Plan> result = planService.getAllPlans();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Base");
        assertThat(result.get(1).getName()).isEqualTo("Premium");
    }

    @Test
    @DisplayName("getAllPlans — restituisce lista vuota se non ci sono piani")
    void getAllPlans_empty() {
        when(planRepository.findAll()).thenReturn(List.of());
        assertThat(planService.getAllPlans()).isEmpty();
    }
}

