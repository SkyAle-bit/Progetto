package com.project.tesi.service;

import com.project.tesi.model.Plan;
import java.util.List;

/**
 * Interfaccia del servizio per la gestione dei piani di abbonamento.
 */
public interface PlanService {

    /** Restituisce tutti i piani commerciali disponibili. */
    List<Plan> getAllPlans();
}