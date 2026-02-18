package com.project.tesi.config;

import com.project.tesi.enums.PlanDuration;
import com.project.tesi.model.Plan;
import com.project.tesi.repository.PlanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(PlanRepository planRepository) {
        return args -> {
            // Logica "Upsert" (Update or Insert):
            // Controlla se il piano esiste. Se esiste lo aggiorna, se no lo crea.

            createOrUpdatePlan(planRepository, "Basic Pack Semestrale", PlanDuration.SEMESTRALE, 1, 1, 960.0, 160.0);
            createOrUpdatePlan(planRepository, "Basic Pack Annuale", PlanDuration.ANNUALE, 1, 1, 1800.0, 150.0);

            createOrUpdatePlan(planRepository, "Premium Pack Semestrale", PlanDuration.SEMESTRALE, 2, 2, 1620.0, 270.0);
            createOrUpdatePlan(planRepository, "Premium Pack Annuale", PlanDuration.ANNUALE, 2, 2, 3000.0, 250.0);

            System.out.println("--- PREZZI PIANI AGGIORNATI CON SUCCESSO ---");
        };
    }

    private void createOrUpdatePlan(PlanRepository repo, String name, PlanDuration duration, int ptCredits, int nutriCredits, double fullPrice, double monthlyPrice) {
        Optional<Plan> existingPlan = repo.findByName(name);

        if (existingPlan.isPresent()) {
            // CASO 1: Il piano esiste già -> AGGIORNA I PREZZI
            Plan plan = existingPlan.get();
            plan.setFullPrice(fullPrice);
            plan.setMonthlyInstallmentPrice(monthlyPrice);
            plan.setMonthlyCreditsPT(ptCredits);
            plan.setMonthlyCreditsNutri(nutriCredits);
            repo.save(plan);
        } else {
            // CASO 2: Il piano non esiste -> CREALO NUOVO
            Plan newPlan = Plan.builder()
                    .name(name)
                    .duration(duration)
                    .monthlyCreditsPT(ptCredits)
                    .monthlyCreditsNutri(nutriCredits)
                    .fullPrice(fullPrice)
                    .monthlyInstallmentPrice(monthlyPrice)
                    .insuranceCoverageDetails("Copertura " + name)
                    .build();
            repo.save(newPlan);
        }
    }
}