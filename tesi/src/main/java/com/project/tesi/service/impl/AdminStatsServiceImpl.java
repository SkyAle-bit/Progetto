package com.project.tesi.service.impl;

import com.project.tesi.enums.Role;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.PlanRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio per le statistiche del pannello Admin.
 *
 * Calcola in sola lettura:
 * <ul>
 *   <li>Distribuzione utenti per ruolo</li>
 *   <li>Nuovi utenti per mese (ultimi 6 mesi)</li>
 *   <li>Piani più sottoscritti con percentuali</li>
 *   <li>Crediti consumati vs disponibili (PT e Nutrizionista)</li>
 *   <li>Fatturato mensile e annuale stimato</li>
 *   <li>Prenotazioni del mese corrente</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AdminStatsServiceImpl implements AdminStatsService {

    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final BookingRepository bookingRepository;
    private final PlanRepository planRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        List<User> allUsers = userRepository.findAll();
        List<Subscription> allSubs = subscriptionRepository.findAll();
        List<Subscription> activeSubs = allSubs.stream().filter(Subscription::isActive).collect(Collectors.toList());
        List<Plan> allPlans = planRepository.findAll();

        // ═══ 1. DISTRIBUZIONE UTENTI PER RUOLO ═══
        Map<String, Long> usersByRole = allUsers.stream()
                .collect(Collectors.groupingBy(u -> u.getRole().name(), Collectors.counting()));
        stats.put("usersByRole", usersByRole);
        stats.put("totalUsers", allUsers.size());

        // ═══ 2. NUOVI UTENTI PER MESE (ultimi 6 mesi) ═══
        List<Map<String, Object>> usersPerMonth = new ArrayList<>();
        YearMonth now = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = now.minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            long count = allUsers.stream()
                    .filter(u -> u.getCreatedAt() != null)
                    .filter(u -> {
                        LocalDate created = u.getCreatedAt().toLocalDate();
                        return !created.isBefore(start) && !created.isAfter(end);
                    })
                    .count();
            Map<String, Object> monthData = new LinkedHashMap<>();
            monthData.put("month", ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ITALIAN));
            monthData.put("year", ym.getYear());
            monthData.put("count", count);
            usersPerMonth.add(monthData);
        }
        stats.put("usersPerMonth", usersPerMonth);

        // ═══ 3. PIANI PIÙ SOTTOSCRITTI ═══
        Map<String, Long> subsByPlan = activeSubs.stream()
                .collect(Collectors.groupingBy(s -> s.getPlan().getName(), Collectors.counting()));
        List<Map<String, Object>> planPopularity = new ArrayList<>();
        long totalActiveSubs = activeSubs.size();
        for (Plan p : allPlans) {
            long count = subsByPlan.getOrDefault(p.getName(), 0L);
            Map<String, Object> planData = new LinkedHashMap<>();
            planData.put("name", p.getName());
            planData.put("activeCount", count);
            planData.put("percentage", totalActiveSubs > 0 ? Math.round((count * 100.0) / totalActiveSubs) : 0);
            planData.put("monthlyPrice", p.getMonthlyInstallmentPrice());
            planData.put("fullPrice", p.getFullPrice());
            planPopularity.add(planData);
        }
        planPopularity.sort((a, b) -> Long.compare((long) b.get("activeCount"), (long) a.get("activeCount")));
        stats.put("planPopularity", planPopularity);
        stats.put("totalActiveSubscriptions", totalActiveSubs);
        stats.put("totalSubscriptions", allSubs.size());

        // ═══ 4. CREDITI CONSUMATI VS DISPONIBILI ═══
        int totalCreditsPTAvail = 0, totalCreditsNutriAvail = 0;
        int totalCreditsPTMax = 0, totalCreditsNutriMax = 0;
        for (Subscription s : activeSubs) {
            totalCreditsPTAvail += s.getCurrentCreditsPT();
            totalCreditsNutriAvail += s.getCurrentCreditsNutri();
            totalCreditsPTMax += s.getPlan().getMonthlyCreditsPT();
            totalCreditsNutriMax += s.getPlan().getMonthlyCreditsNutri();
        }
        Map<String, Object> credits = new LinkedHashMap<>();
        credits.put("ptAvailable", totalCreditsPTAvail);
        credits.put("ptTotal", totalCreditsPTMax);
        credits.put("ptConsumed", totalCreditsPTMax - totalCreditsPTAvail);
        credits.put("ptPercentUsed", totalCreditsPTMax > 0 ? Math.round(((totalCreditsPTMax - totalCreditsPTAvail) * 100.0) / totalCreditsPTMax) : 0);
        credits.put("nutriAvailable", totalCreditsNutriAvail);
        credits.put("nutriTotal", totalCreditsNutriMax);
        credits.put("nutriConsumed", totalCreditsNutriMax - totalCreditsNutriAvail);
        credits.put("nutriPercentUsed", totalCreditsNutriMax > 0 ? Math.round(((totalCreditsNutriMax - totalCreditsNutriAvail) * 100.0) / totalCreditsNutriMax) : 0);
        stats.put("credits", credits);

        // ═══ 5. FATTURATO STIMATO ═══
        double monthlyRevenue = activeSubs.stream()
                .mapToDouble(s -> s.getPlan().getMonthlyInstallmentPrice())
                .sum();
        double yearlyRevenue = monthlyRevenue * 12;
        stats.put("monthlyRevenue", Math.round(monthlyRevenue * 100.0) / 100.0);
        stats.put("yearlyRevenue", Math.round(yearlyRevenue * 100.0) / 100.0);

        // ═══ 6. PRENOTAZIONI TOTALI (questo mese) ═══
        List<Booking> allBookings = bookingRepository.findAll();
        YearMonth thisMonth = YearMonth.now();
        long bookingsThisMonth = allBookings.stream()
                .filter(b -> b.getBookedAt() != null)
                .filter(b -> YearMonth.from(b.getBookedAt()).equals(thisMonth))
                .count();
        stats.put("bookingsThisMonth", bookingsThisMonth);
        stats.put("bookingsTotal", allBookings.size());

        // ═══ 7. CARICO PROFESSIONISTI ═══
        List<Map<String, Object>> proWorkload = new ArrayList<>();
        List<User> professionals = allUsers.stream()
                .filter(u -> u.getRole() == Role.PERSONAL_TRAINER || u.getRole() == Role.NUTRITIONIST)
                .collect(Collectors.toList());
        for (User pro : professionals) {
            long clientCount;
            if (pro.getRole() == Role.PERSONAL_TRAINER) {
                clientCount = allUsers.stream().filter(u -> u.getAssignedPT() != null && u.getAssignedPT().getId().equals(pro.getId())).count();
            } else {
                clientCount = allUsers.stream().filter(u -> u.getAssignedNutritionist() != null && u.getAssignedNutritionist().getId().equals(pro.getId())).count();
            }
            Map<String, Object> pw = new LinkedHashMap<>();
            pw.put("name", pro.getFirstName() + " " + pro.getLastName());
            pw.put("role", pro.getRole().name());
            pw.put("clientCount", clientCount);
            proWorkload.add(pw);
        }
        proWorkload.sort((a, b) -> Long.compare((long) b.get("clientCount"), (long) a.get("clientCount")));
        stats.put("professionalWorkload", proWorkload);

        return stats;
    }
}

