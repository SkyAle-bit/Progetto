package com.project.tesi.service;

import org.springframework.validation.annotation.Validated;

import com.project.tesi.model.Plan;
import java.util.List;

/**
 * Interfaccia del servizio per la gestione dei piani di abbonamento.
 */
@Validated
public interface PlanService {

    List<Plan> getAllPlans();

    Plan getPlanById(Long id);

    Plan createPlan(Plan plan);

    Plan updatePlan(Long id, Plan updated);

    void deletePlan(Long id);
}