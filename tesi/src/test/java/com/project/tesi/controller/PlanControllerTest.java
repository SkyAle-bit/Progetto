package com.project.tesi.controller;

import com.project.tesi.model.Plan;
import com.project.tesi.enums.PlanDuration;
import com.project.tesi.service.PlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test unitari per {@link PlanController}.
 */
@ExtendWith(MockitoExtension.class)
class PlanControllerTest {

    @Mock private PlanService planService;

    @InjectMocks
    private PlanController planController;

    @Test
    @DisplayName("getAllPlans — restituisce 200 con lista piani")
    void getAllPlans() {
        Plan p = Plan.builder().id(1L).name("Premium").duration(PlanDuration.ANNUALE).build();
        when(planService.getAllPlans()).thenReturn(List.of(p));

        ResponseEntity<List<Plan>> response = planController.getAllPlans();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).isEqualTo("Premium");
    }
}

