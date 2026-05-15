package com.project.tesi.controller;

import com.project.tesi.facade.PlanFacade;
import com.project.tesi.model.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint REST per i piani di abbonamento (pubblico).
 */
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanFacade planFacade;

    /** Restituisce la lista di tutti i piani di abbonamento disponibili. */
    @GetMapping
    public ResponseEntity<List<Plan>> getAllPlans() {
        return ResponseEntity.ok(planFacade.getAllPlans());
    }
}
