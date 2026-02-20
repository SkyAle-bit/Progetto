package com.project.tesi.service.impl;

import com.project.tesi.model.Plan;
import com.project.tesi.repository.PlanRepository;
import com.project.tesi.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Override
    public List<Plan> getAllPlans() {
        // Restituisce tutti i piani salvati nel database
        return planRepository.findAll();
    }
}