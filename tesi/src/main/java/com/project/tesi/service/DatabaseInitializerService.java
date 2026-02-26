package com.project.tesi.service;

import com.project.tesi.enums.*;
import com.project.tesi.model.*;
import com.project.tesi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DatabaseInitializerService {

        private final PlanRepository planRepository;
        private final UserRepository userRepository;
        private final WeeklyScheduleRepository weeklyScheduleRepository;
        private final SlotRepository slotRepository;
        private final SubscriptionRepository subscriptionRepository;
        private final BookingRepository bookingRepository;
        private final ReviewRepository reviewRepository;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public void initialize() {
                // Svuota esplicitamente tutte le tabelle per evitare ConstraintViolations
                bookingRepository.deleteAll();
                slotRepository.deleteAll();
                weeklyScheduleRepository.deleteAll();
                reviewRepository.deleteAll();
                subscriptionRepository.deleteAll();
                userRepository.deleteAll();
                planRepository.deleteAll();

                // 1. Piani
                createOrUpdatePlan("Basic Pack Semestrale", PlanDuration.SEMESTRALE, 1, 1, 960.0, 160.0);
                createOrUpdatePlan("Basic Pack Annuale", PlanDuration.ANNUALE, 1, 1, 1800.0, 150.0);
                createOrUpdatePlan("Premium Pack Semestrale", PlanDuration.SEMESTRALE, 2, 2, 1620.0, 270.0);
                createOrUpdatePlan("Premium Pack Annuale", PlanDuration.ANNUALE, 2, 2, 3000.0, 250.0);

                Plan basicS = planRepository.findByName("Basic Pack Semestrale").orElseThrow();
                Plan basicA = planRepository.findByName("Basic Pack Annuale").orElseThrow();
                Plan premiumS = planRepository.findByName("Premium Pack Semestrale").orElseThrow();
                Plan premiumA = planRepository.findByName("Premium Pack Annuale").orElseThrow();

                // 2. Personal Trainer (2)
                User pt1 = createUser("Marco", "Rossi", "pt1@test.com", Role.PERSONAL_TRAINER,
                                "Specializzato in allenamento funzionale e riabilitazione.", null, null);
                User pt2 = createUser("Giulia", "Bianchi", "pt2@test.com", Role.PERSONAL_TRAINER,
                                "Esperta in powerlifting e preparazione atletica.", null, null);

                // 3. Nutrizionisti (2)
                User nut1 = createUser("Laura", "Verdi", "nutri1@test.com", Role.NUTRITIONIST,
                                "Biologa nutrizionista, esperta in dieta mediterranea e sportiva.", null, null);
                User nut2 = createUser("Andrea", "Esposito", "nutri2@test.com", Role.NUTRITIONIST,
                                "Specializzato in nutrizione clinica e intolleranze alimentari.", null, null);

                // 4. Clienti (4)
                User c1 = createUser("Luca", "Ferri", "luca@test.com", Role.CLIENT, null, pt1, nut1);
                User c2 = createUser("Sofia", "Conti", "sofia@test.com", Role.CLIENT, null, pt1, nut2);
                User c3 = createUser("Matteo", "Galli", "matteo@test.com", Role.CLIENT, null, pt2, nut1);
                User c4 = createUser("Chiara", "Fontana", "chiara@test.com", Role.CLIENT, null, pt2, nut2);

                // 5. Abbonamenti
                Plan[] plans = { basicS, basicA, premiumS, premiumA };
                PaymentFrequency[] freqs = { PaymentFrequency.UNICA_SOLUZIONE, PaymentFrequency.RATE_MENSILI };
                User[] clients = { c1, c2, c3, c4 };
                for (int i = 0; i < clients.length; i++) {
                        createSubscription(clients[i], plans[i % 4], freqs[i % 2]);
                }

                // 6. Recensioni
                createReview(c1, pt1, 5, "Ottimo preparatore, molto attento!");
                createReview(c2, pt1, 4, "Bravo, ma a volte in ritardo.");
                createReview(c3, pt2, 5, "Super professionale!");
                createReview(c4, pt2, 5, "Motivante e preparato.");

                createReview(c1, nut1, 5, "Piano alimentare perfetto.");
                createReview(c2, nut2, 4, "Qualche ricetta poco chiara.");
                createReview(c3, nut1, 5, "La dieta funziona alla grande!");
                createReview(c4, nut2, 5, "Disponibile e competente.");

                // 7. Orari settimanali
                createWeeklySchedule(pt1, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(pt1, DayOfWeek.WEDNESDAY, LocalTime.of(15, 0), LocalTime.of(19, 0));
                createWeeklySchedule(pt1, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(pt2, DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                createWeeklySchedule(pt2, DayOfWeek.THURSDAY, LocalTime.of(16, 0), LocalTime.of(20, 0));
                createWeeklySchedule(pt2, DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(12, 0));

                createWeeklySchedule(nut1, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
                createWeeklySchedule(nut1, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut1, DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                createWeeklySchedule(nut2, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut2, DayOfWeek.THURSDAY, LocalTime.of(15, 0), LocalTime.of(19, 0));
                createWeeklySchedule(nut2, DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(13, 0));

                // 8. Genera slot (prossimi 7 giorni)
                LocalDate start = LocalDate.now().plusDays(1);
                LocalDate end = start.plusDays(6);
                User[] pros = { pt1, pt2, nut1, nut2 };
                for (User pro : pros)
                        generateSlotsForProfessional(pro, start, end);

                // 9. Prenotazioni campione
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(pt1), 0, c1, pt1, true);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(pt1), 1, c2, pt1, true);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(pt2), 0, c3, pt2, true);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(pt2), 1, c4, pt2, true);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(nut1), 0, c1, nut1, false);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(nut1), 1, c3, nut1, false);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(nut2), 0, c2, nut2, false);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(nut2), 1, c4, nut2, false);

                // Prenotazione passata (COMPLETED)
                LocalDateTime pastTime = LocalDateTime.now().minusDays(2).withHour(10).withMinute(0);
                Slot pastSlot = slotRepository.save(Slot.builder().professional(pt1)
                                .startTime(pastTime).endTime(pastTime.plusMinutes(30)).isBooked(true).build());

                String pastLink = "https://meet.jit.si/SkyAle_Consulto_" + c4.getId() + "_" + pt1.getId() + "_"
                                + UUID.randomUUID().toString().substring(0, 8);

                bookingRepository.save(Booking.builder().user(c4).professional(pt1).slot(pastSlot)
                                .meetingLink(pastLink).status(BookingStatus.COMPLETED).build());
        }

        // --- METODI HELPER ---

        private void createOrUpdatePlan(String name, PlanDuration duration, int ptCredits, int nutriCredits,
                        double fullPrice, double monthlyPrice) {
                if (planRepository.findByName(name).isEmpty()) {
                        Plan plan = Plan.builder()
                                        .name(name)
                                        .duration(duration)
                                        .monthlyCreditsPT(ptCredits)
                                        .monthlyCreditsNutri(nutriCredits)
                                        .fullPrice(fullPrice)
                                        .monthlyInstallmentPrice(monthlyPrice)
                                        .insuranceCoverageDetails("Copertura inclusa")
                                        .build();
                        planRepository.save(plan);
                }
        }

        private User createUser(String firstName, String lastName, String email, Role role,
                        String bio, User assignedPT, User assignedNutritionist) {
                return userRepository.findByEmail(email).orElseGet(() -> {
                        User user = User.builder()
                                        .firstName(firstName)
                                        .lastName(lastName)
                                        .email(email)
                                        .password(passwordEncoder.encode("password"))
                                        .role(role)
                                        .professionalBio(bio)
                                        .assignedPT(assignedPT)
                                        .assignedNutritionist(assignedNutritionist)
                                        .build();
                        return userRepository.save(user);
                });
        }

        private void createSubscription(User user, Plan plan, PaymentFrequency frequency) {
                if (subscriptionRepository.findByUserAndActiveTrue(user).isEmpty()) {
                        LocalDate start = LocalDate.now();
                        LocalDate end = plan.getDuration() == PlanDuration.ANNUALE ? start.plusYears(1)
                                        : start.plusMonths(6);
                        int totalInstallments = frequency == PaymentFrequency.UNICA_SOLUZIONE ? 1
                                        : plan.getDuration().getMonths();

                        Subscription sub = Subscription.builder()
                                        .user(user)
                                        .plan(plan)
                                        .paymentFrequency(frequency)
                                        .startDate(start)
                                        .endDate(end)
                                        .active(true)
                                        .currentCreditsPT(plan.getMonthlyCreditsPT())
                                        .currentCreditsNutri(plan.getMonthlyCreditsNutri())
                                        .lastRenewalDate(start)
                                        .installmentsPaid(frequency == PaymentFrequency.UNICA_SOLUZIONE ? 1 : 1)
                                        .totalInstallments(totalInstallments)
                                        .nextPaymentDate(
                                                        frequency == PaymentFrequency.RATE_MENSILI ? start.plusMonths(1)
                                                                        : null)
                                        .build();
                        subscriptionRepository.save(sub);
                }
        }

        private void createReview(User client, User professional, int rating, String comment) {
                if (!reviewRepository.existsByClientIdAndProfessionalId(client.getId(), professional.getId())) {
                        Review review = Review.builder()
                                        .client(client)
                                        .professional(professional)
                                        .rating(rating)
                                        .comment(comment)
                                        .build();
                        reviewRepository.save(review);
                }
        }

        private void createWeeklySchedule(User pro, DayOfWeek day, LocalTime start, LocalTime end) {
                weeklyScheduleRepository.save(WeeklySchedule.builder()
                                .professional(pro)
                                .dayOfWeek(day)
                                .startTime(start)
                                .endTime(end)
                                .build());
        }

        private void generateSlotsForProfessional(User pro, LocalDate startDate, LocalDate endDate) {
                List<WeeklySchedule> schedules = weeklyScheduleRepository.findByProfessional(pro);
                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                        LocalDate current = date;
                        schedules.stream()
                                        .filter(s -> s.getDayOfWeek().equals(current.getDayOfWeek()))
                                        .forEach(rule -> {
                                                LocalTime time = rule.getStartTime();
                                                while (time.isBefore(rule.getEndTime())) {
                                                        LocalDateTime slotStart = LocalDateTime.of(current, time);
                                                        if (!slotRepository.existsByProfessionalAndStartTime(pro,
                                                                        slotStart)) {
                                                                Slot slot = Slot.builder()
                                                                                .professional(pro)
                                                                                .startTime(slotStart)
                                                                                .endTime(slotStart.plusMinutes(30))
                                                                                .isBooked(false)
                                                                                .build();
                                                                slotRepository.save(slot);
                                                        }
                                                        time = time.plusMinutes(30);
                                                }
                                        });
                }
        }

        private void bookSlot(List<Slot> freeSlots, int index, User client, User professional, boolean isPT) {
                if (freeSlots == null || freeSlots.size() <= index)
                        return;
                Slot slot = freeSlots.get(index);
                slot.setBooked(true);
                slotRepository.save(slot);
                subscriptionRepository.findByUserAndActiveTrue(client).ifPresent(sub -> {
                        if (isPT) {
                                if (sub.getCurrentCreditsPT() > 0)
                                        sub.setCurrentCreditsPT(sub.getCurrentCreditsPT() - 1);
                        } else {
                                if (sub.getCurrentCreditsNutri() > 0)
                                        sub.setCurrentCreditsNutri(sub.getCurrentCreditsNutri() - 1);
                        }
                        subscriptionRepository.save(sub);
                });

                String meetLink = "https://meet.jit.si/SkyAle_Consulto_" + client.getId() + "_" + professional.getId()
                                + "_" + UUID.randomUUID().toString().substring(0, 8);

                bookingRepository.save(Booking.builder()
                                .user(client)
                                .professional(professional)
                                .slot(slot)
                                .meetingLink(meetLink)
                                .status(BookingStatus.CONFIRMED)
                                .build());
        }
}

