package com.project.tesi.controller;

import com.project.tesi.model.Plan;
import com.project.tesi.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST per la consultazione dei piani di abbonamento.
 * Endpoint pubblico usato durante la registrazione e nella pagina dei prezzi.
 */
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    /** Restituisce la lista di tutti i piani di abbonamento disponibili. */
    @GetMapping
    public ResponseEntity<List<Plan>> getAllPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }
}