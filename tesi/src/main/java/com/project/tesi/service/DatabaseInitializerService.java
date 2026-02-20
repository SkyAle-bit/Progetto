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
        // 1. Piani
        createOrUpdatePlan("Basic Pack Semestrale", PlanDuration.SEMESTRALE, 1, 1, 960.0, 160.0);
        createOrUpdatePlan("Basic Pack Annuale", PlanDuration.ANNUALE, 1, 1, 1800.0, 150.0);
        createOrUpdatePlan("Premium Pack Semestrale", PlanDuration.SEMESTRALE, 2, 2, 1620.0, 270.0);
        createOrUpdatePlan("Premium Pack Annuale", PlanDuration.ANNUALE, 2, 2, 3000.0, 250.0);

        // 2. Recupera piani
        Plan basicSemestrale = planRepository.findByName("Basic Pack Semestrale").orElseThrow();
        Plan basicAnnuale = planRepository.findByName("Basic Pack Annuale").orElseThrow();
        Plan premiumSemestrale = planRepository.findByName("Premium Pack Semestrale").orElseThrow();
        Plan premiumAnnuale = planRepository.findByName("Premium Pack Annuale").orElseThrow();

        // 3. Professionisti
        User pt1 = createUser("Marco", "Rossi", "pt1@test.com", Role.PERSONAL_TRAINER,
                "Specializzato in allenamento funzionale e riabilitazione.", null, null);
        User pt2 = createUser("Giulia", "Bianchi", "pt2@test.com", Role.PERSONAL_TRAINER,
                "Esperta in powerlifting e preparazione atletica.", null, null);
        User nut1 = createUser("Laura", "Verdi", "nutri1@test.com", Role.NUTRITIONIST,
                "Biologa nutrizionista, esperta in dieta mediterranea e sportiva.", null, null);
        User nut2 = createUser("Andrea", "Esposito", "nutri2@test.com", Role.NUTRITIONIST,
                "Specializzato in nutrizione clinica e intolleranze alimentari.", null, null);

        // 4. Clienti
        User client1 = createUser("Luca", "Ferri", "luca@test.com", Role.CLIENT, null, pt1, nut1);
        User client2 = createUser("Sofia", "Conti", "sofia@test.com", Role.CLIENT, null, pt1, nut2);
        User client3 = createUser("Matteo", "Galli", "matteo@test.com", Role.CLIENT, null, pt2, nut1);
        User client4 = createUser("Chiara", "Fontana", "chiara@test.com", Role.CLIENT, null, pt2, nut2);
        User client5 = createUser("Alessandro", "Moretti", "alessandro@test.com", Role.CLIENT, null, pt1, nut1);

        // 5. Abbonamenti
        createSubscription(client1, basicSemestrale, PaymentFrequency.UNICA_SOLUZIONE);
        createSubscription(client2, basicAnnuale, PaymentFrequency.RATE_MENSILI);
        createSubscription(client3, premiumSemestrale, PaymentFrequency.UNICA_SOLUZIONE);
        createSubscription(client4, premiumAnnuale, PaymentFrequency.RATE_MENSILI);
        createSubscription(client5, basicSemestrale, PaymentFrequency.RATE_MENSILI);

        // 6. Recensioni
        createReview(client1, pt1, 5, "Ottimo preparatore, molto attento!");
        createReview(client2, pt1, 4, "Bravo, ma a volte in ritardo.");
        createReview(client3, pt2, 5, "Super professionale!");
        createReview(client4, pt2, 5, "Motivante e preparato.");
        createReview(client5, pt1, 4, "Buono, consigliato.");
        createReview(client1, nut1, 5, "Piano alimentare perfetto.");
        createReview(client2, nut2, 4, "Buona, ma qualche ricetta poco chiara.");
        createReview(client3, nut1, 5, "La dieta funziona alla grande!");
        createReview(client4, nut2, 5, "Disponibile e competente.");
        createReview(client5, nut1, 4, "Soddisfatto, risultati visibili.");

        // 7. Disponibilità settimanali
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

        // 8. Generazione slot (prossimi 7 giorni)
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = start.plusDays(6);
        generateSlotsForProfessional(pt1, start, end);
        generateSlotsForProfessional(pt2, start, end);
        generateSlotsForProfessional(nut1, start, end);
        generateSlotsForProfessional(nut2, start, end);

        // 9. Prenotazioni
        createBookings(client1, client2, client3, client4, pt1, pt2, nut1, nut2);
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
            LocalDate end = plan.getDuration() == PlanDuration.ANNUALE ? start.plusYears(1) : start.plusMonths(6);
            int totalInstallments = frequency == PaymentFrequency.UNICA_SOLUZIONE ? 1 : plan.getDuration().getMonths();

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
                    .nextPaymentDate(frequency == PaymentFrequency.RATE_MENSILI ? start.plusMonths(1) : null)
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
                            if (!slotRepository.existsByProfessionalAndStartTime(pro, slotStart)) {
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

    private void createBookings(User client1, User client2, User client3, User client4,
                                User pt1, User pt2, User nut1, User nut2) {
        // PT1 slots
        List<Slot> freeSlotsPt1 = slotRepository.findByProfessionalAndIsBookedFalse(pt1);
        if (!freeSlotsPt1.isEmpty()) {
            Slot slot = freeSlotsPt1.get(0);
            slot.setBooked(true);
            slotRepository.save(slot);
            subscriptionRepository.findByUserAndActiveTrue(client1).ifPresent(sub -> {
                sub.setCurrentCreditsPT(sub.getCurrentCreditsPT() - 1);
                subscriptionRepository.save(sub);
            });
            Booking booking = Booking.builder()
                    .user(client1)
                    .professional(pt1)
                    .slot(slot)
                    .meetingLink("https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 10))
                    .status(BookingStatus.CONFIRMED)
                    .build();
            bookingRepository.save(booking);
        }

        if (freeSlotsPt1.size() > 1) {
            Slot slot = freeSlotsPt1.get(1);
            slot.setBooked(true);
            slotRepository.save(slot);
            subscriptionRepository.findByUserAndActiveTrue(client2).ifPresent(sub -> {
                sub.setCurrentCreditsPT(sub.getCurrentCreditsPT() - 1);
                subscriptionRepository.save(sub);
            });
            Booking booking = Booking.builder()
                    .user(client2)
                    .professional(pt1)
                    .slot(slot)
                    .meetingLink("https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 10))
                    .status(BookingStatus.CONFIRMED)
                    .build();
            bookingRepository.save(booking);
        }

        // Nutri1 slots
        List<Slot> freeSlotsNut1 = slotRepository.findByProfessionalAndIsBookedFalse(nut1);
        if (!freeSlotsNut1.isEmpty()) {
            Slot slot = freeSlotsNut1.get(0);
            slot.setBooked(true);
            slotRepository.save(slot);
            subscriptionRepository.findByUserAndActiveTrue(client1).ifPresent(sub -> {
                sub.setCurrentCreditsNutri(sub.getCurrentCreditsNutri() - 1);
                subscriptionRepository.save(sub);
            });
            Booking booking = Booking.builder()
                    .user(client1)
                    .professional(nut1)
                    .slot(slot)
                    .meetingLink("https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 10))
                    .status(BookingStatus.CONFIRMED)
                    .build();
            bookingRepository.save(booking);
        }

        // PT2 slots
        List<Slot> freeSlotsPt2 = slotRepository.findByProfessionalAndIsBookedFalse(pt2);
        if (!freeSlotsPt2.isEmpty()) {
            Slot slot = freeSlotsPt2.get(0);
            slot.setBooked(true);
            slotRepository.save(slot);
            subscriptionRepository.findByUserAndActiveTrue(client3).ifPresent(sub -> {
                sub.setCurrentCreditsPT(sub.getCurrentCreditsPT() - 1);
                subscriptionRepository.save(sub);
            });
            Booking booking = Booking.builder()
                    .user(client3)
                    .professional(pt2)
                    .slot(slot)
                    .meetingLink("https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 10))
                    .status(BookingStatus.CONFIRMED)
                    .build();
            bookingRepository.save(booking);
        }

        // Nutri2 slots
        List<Slot> freeSlotsNut2 = slotRepository.findByProfessionalAndIsBookedFalse(nut2);
        if (!freeSlotsNut2.isEmpty()) {
            Slot slot = freeSlotsNut2.get(0);
            slot.setBooked(true);
            slotRepository.save(slot);
            subscriptionRepository.findByUserAndActiveTrue(client4).ifPresent(sub -> {
                sub.setCurrentCreditsNutri(sub.getCurrentCreditsNutri() - 1);
                subscriptionRepository.save(sub);
            });
            Booking booking = Booking.builder()
                    .user(client4)
                    .professional(nut2)
                    .slot(slot)
                    .meetingLink("https://meet.google.com/" + UUID.randomUUID().toString().substring(0, 10))
                    .status(BookingStatus.CONFIRMED)
                    .build();
            bookingRepository.save(booking);
        }

        // Prenotazione passata
        LocalDateTime pastTime = LocalDateTime.now().minusDays(2).withHour(10).withMinute(0);
        Slot pastSlot = Slot.builder()
                .professional(pt1)
                .startTime(pastTime)
                .endTime(pastTime.plusMinutes(30))
                .isBooked(true)
                .build();
        pastSlot = slotRepository.save(pastSlot);
        Booking pastBooking = Booking.builder()
                .user(client4)
                .professional(pt1)
                .slot(pastSlot)
                .meetingLink("https://meet.google.com/past-meeting")
                .status(BookingStatus.COMPLETED)
                .build();
        bookingRepository.save(pastBooking);
    }
}