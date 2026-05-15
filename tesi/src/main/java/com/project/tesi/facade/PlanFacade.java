package com.project.tesi.facade;

import com.project.tesi.model.Plan;
import com.project.tesi.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanFacade {

    private final PlanService planService;

    public List<Plan> getAllPlans() {
        return planService.getAllPlans();
    }
}
