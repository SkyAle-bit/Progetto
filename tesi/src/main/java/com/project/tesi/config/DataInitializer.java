package com.project.tesi.config;

import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.enums.PlanDuration;
import com.project.tesi.enums.Role;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Slot;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.PlanRepository;
import com.project.tesi.repository.SlotRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(PlanRepository planRepository,
                               UserRepository userRepository,
                               SlotRepository slotRepository,
                               SubscriptionRepository subscriptionRepository) {
        return args -> {
            // 1. GESTIONE PIANI (La tua logica specifica)
            createOrUpdatePlan(planRepository, "Basic Pack Semestrale", PlanDuration.SEMESTRALE, 1, 1, 960.0, 160.0);
            createOrUpdatePlan(planRepository, "Basic Pack Annuale", PlanDuration.ANNUALE, 1, 1, 1800.0, 150.0);
            createOrUpdatePlan(planRepository, "Premium Pack Semestrale", PlanDuration.SEMESTRALE, 2, 2, 1620.0, 270.0);
            createOrUpdatePlan(planRepository, "Premium Pack Annuale", PlanDuration.ANNUALE, 2, 2, 3000.0, 250.0);

            // 2. CREAZIONE UTENTI (PT, Nutrizionista, Cliente)
            User pt = createOrUpdateUser(userRepository, "Mario", "Rossi", "pt@test.com", Role.PERSONAL_TRAINER);
            User nutri = createOrUpdateUser(userRepository, "Laura", "Verdi", "nutri@test.com", Role.NUTRITIONIST);
            User client = createOrUpdateUser(userRepository, "Luigi", "Bianchi", "client@test.com", Role.CLIENT);

            // 3. CREAZIONE SLOT (Disponibilità per domani)
            if (slotRepository.count() == 0) {
                LocalDateTime tomorrow = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0));

                createSlot(slotRepository, pt, tomorrow);                  // 10:00 - 10:30
                createSlot(slotRepository, pt, tomorrow.plusMinutes(30));  // 10:30 - 11:00
                createSlot(slotRepository, pt, tomorrow.plusMinutes(60));  // 11:00 - 11:30

                // Nutrizionista: Slot dalle 15:00
                LocalDateTime tomorrowAfternoon = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(15, 0));
                createSlot(slotRepository, nutri, tomorrowAfternoon);                 // 15:00 - 15:30
                createSlot(slotRepository, nutri, tomorrowAfternoon.plusMinutes(30)); // 15:30 - 16:00
            }

            // 4. CREAZIONE ABBONAMENTO CLIENTE (Per testare le prenotazioni)
            if (subscriptionRepository.findByUserAndIsActiveTrue(client).isEmpty()) {
                // Recuperiamo uno dei piani appena creati (es. Basic Semestrale)
                Optional<Plan> selectedPlan = planRepository.findByName("Basic Pack Semestrale");

                if (selectedPlan.isPresent()) {
                    Plan plan = selectedPlan.get();
                    Subscription sub = Subscription.builder()
                            .user(client)
                            .plan(plan)
                            .startDate(LocalDate.now())
                            .endDate(LocalDate.now().plusMonths(6))
                            .isActive(true) // Nota: assicurati che in Subscription il campo sia 'active' o 'isActive'
                            .paymentFrequency(PaymentFrequency.RATE_MENSILI) // Assumo esista questo Enum
                            .currentCreditsPT(plan.getMonthlyCreditsPT())       // Usa i campi corretti del tuo Plan
                            .currentCreditsNutri(plan.getMonthlyCreditsNutri())
                            .build();

                    subscriptionRepository.save(sub);
                }
            }
        };
    }

    // --- METODI HELPER ---

    // La tua logica originale per i Piani
    private void createOrUpdatePlan(PlanRepository repo, String name, PlanDuration duration, int ptCredits, int nutriCredits, double fullPrice, double monthlyPrice) {
        Optional<Plan> existingPlan = repo.findByName(name);

        if (existingPlan.isPresent()) {
            Plan plan = existingPlan.get();
            plan.setFullPrice(fullPrice);
            plan.setMonthlyInstallmentPrice(monthlyPrice);
            plan.setMonthlyCreditsPT(ptCredits);
            plan.setMonthlyCreditsNutri(nutriCredits);
            repo.save(plan);
        } else {
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

    private User createOrUpdateUser(UserRepository repo, String firstName, String lastName, String email, Role role) {
        Optional<User> existing = repo.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .password("password") // Password temporanea
                .role(role)
                .build();
        return repo.save(user);
    }

    private void createSlot(SlotRepository repo, User pro, LocalDateTime start) {
        Slot slot = Slot.builder()
                .professional(pro)
                .startTime(start)
                .endTime(start.plusMinutes(60))
                .isBooked(false) // O 'isBooked(false)' in base alla tua entità Slot
                .build();
        repo.save(slot);
    }
}