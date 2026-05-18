package com.project.tesi.facade;

import com.project.tesi.model.Plan;

import java.util.List;

public interface IPlanFacade {
    List<Plan> getAllPlans();

    Plan getPlanById(Long id);

    Plan createPlan(Plan plan);

    Plan updatePlan(Long id, Plan updated);

    void deletePlan(Long id);
}
