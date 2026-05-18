package com.project.tesi.service;

import com.project.tesi.enums.*;
import com.project.tesi.model.*;
import com.project.tesi.repository.*;
import com.project.tesi.builder.BookingDirector;
import jakarta.persistence.EntityManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Servizio per il reset e la ripopolazione del database con dati di test.
 *
 * Svuota tutte le tabelle nell'ordine corretto (rispettando le FK)
 * e le ripopola con utenti, piani, abbonamenti, slot, prenotazioni,
 * recensioni e messaggi di esempio con link Jitsi per le videochiamate.
 *
 * Utilizzato solo in fase di sviluppo/demo tramite l'endpoint
 * {@code GET /api/bookings/reset-database}.
 */
@Service
public class DatabaseInitializerService {

        private final PlanRepository planRepository;
        private final UserRepository userRepository;
        private final WeeklyScheduleRepository weeklyScheduleRepository;
        private final SlotRepository slotRepository;
        private final SubscriptionRepository subscriptionRepository;
        private final BookingRepository bookingRepository;
        private final ReviewRepository reviewRepository;
        private final MessageRepository messageRepository;
        private final ChatRepository chatRepository;
        private final DocumentRepository documentRepository;
        private final PasswordEncoder passwordEncoder;
        private final EntityManager entityManager;
        private final JdbcTemplate jdbcTemplate;
        private final BookingDirector bookingDirector;

        public DatabaseInitializerService(PlanRepository planRepository,
                                          UserRepository userRepository,
                                          WeeklyScheduleRepository weeklyScheduleRepository,
                                          SlotRepository slotRepository,
                                          SubscriptionRepository subscriptionRepository,
                                          BookingRepository bookingRepository,
                                          ReviewRepository reviewRepository,
                                          MessageRepository messageRepository,
                                          ChatRepository chatRepository,
                                          DocumentRepository documentRepository,
                                          PasswordEncoder passwordEncoder,
                                          EntityManager entityManager,
                                          JdbcTemplate jdbcTemplate,
                                          BookingDirector bookingDirector) {
                this.planRepository = planRepository;
                this.userRepository = userRepository;
                this.weeklyScheduleRepository = weeklyScheduleRepository;
                this.slotRepository = slotRepository;
                this.subscriptionRepository = subscriptionRepository;
                this.bookingRepository = bookingRepository;
                this.reviewRepository = reviewRepository;
                this.messageRepository = messageRepository;
                this.chatRepository = chatRepository;
                this.documentRepository = documentRepository;
                this.passwordEncoder = passwordEncoder;
                this.entityManager = entityManager;
                this.jdbcTemplate = jdbcTemplate;
                this.bookingDirector = bookingDirector;
        }

        @Transactional
        public void initialize() {
                dropLegacySlotIdUniqueConstraint();
                ensureUsersRoleCheckSupportsModerator();

                try {
                        jdbcTemplate.execute("DROP TABLE IF EXISTS chat_messages CASCADE");
                } catch (Exception ignored) {
                }

                messageRepository.deleteAllInBatch();
                chatRepository.deleteAllInBatch();
                bookingRepository.deleteAllInBatch();
                slotRepository.deleteAllInBatch();
                weeklyScheduleRepository.deleteAllInBatch();
                reviewRepository.deleteAllInBatch();
                subscriptionRepository.deleteAllInBatch();
                documentRepository.deleteAllInBatch();
                userRepository.deleteAllInBatch();
                planRepository.deleteAllInBatch();
                entityManager.flush();
                entityManager.clear();

                createOrUpdatePlan("Basic Pack Semestrale", PlanDuration.SEMESTRALE, 1, 1, 960.0, 160.0);
                createOrUpdatePlan("Basic Pack Annuale", PlanDuration.ANNUALE, 1, 1, 1800.0, 150.0);
                createOrUpdatePlan("Premium Pack Semestrale", PlanDuration.SEMESTRALE, 2, 2, 1620.0, 270.0);
                createOrUpdatePlan("Premium Pack Annuale", PlanDuration.ANNUALE, 2, 2, 3000.0, 250.0);

                Plan basicS = planRepository.findByName("Basic Pack Semestrale").orElseThrow();
                Plan basicA = planRepository.findByName("Basic Pack Annuale").orElseThrow();
                Plan premiumS = planRepository.findByName("Premium Pack Semestrale").orElseThrow();
                Plan premiumA = planRepository.findByName("Premium Pack Annuale").orElseThrow();

                User pt1 = createUser("Marco", "Rossi", "pt1@test.com", Role.PERSONAL_TRAINER,
                                "Specializzato in allenamento funzionale e riabilitazione.", null, null);
                User pt2 = createUser("Giulia", "Bianchi", "pt2@test.com", Role.PERSONAL_TRAINER,
                                "Esperta in powerlifting e preparazione atletica.", null, null);

                User nut1 = createUser("Laura", "Verdi", "nutri1@test.com", Role.NUTRITIONIST,
                                "Biologa nutrizionista, esperta in dieta mediterranea e sportiva.", null, null);
                User nut2 = createUser("Andrea", "Esposito", "nutri2@test.com", Role.NUTRITIONIST,
                                "Specializzato in nutrizione clinica e intolleranze alimentari.", null, null);

                User c1 = createUser("Luca", "Ferri", "luca@test.com", Role.CLIENT, null, pt1, nut1);
                User c2 = createUser("Sofia", "Conti", "sofia@test.com", Role.CLIENT, null, pt1, nut2);
                User c3 = createUser("Matteo", "Galli", "matteo@test.com", Role.CLIENT, null, pt2, nut1);
                User c4 = createUser("Chiara", "Fontana", "chiara@test.com", Role.CLIENT, null, pt2, nut2);

                User cTest = createUser("Test", "Recensore", "testreview@test.com", Role.CLIENT, null, pt1, nut1);
                jdbcTemplate.update(
                                "UPDATE users SET created_at = ? WHERE id = ?",
                                java.sql.Timestamp.valueOf(java.time.LocalDateTime.now().minusDays(40)),
                                cTest.getId());
                Plan basicSTest = planRepository.findByName("Basic Pack Semestrale").orElseThrow();
                createSubscription(cTest, basicSTest, PaymentFrequency.UNICA_SOLUZIONE);

                createUser("Admin", "Sistema", "admin@test.com", Role.ADMIN, null, null, null);
                createUser("Paolo", "Assicurazioni", "insurance@test.com", Role.INSURANCE_MANAGER, null, null, null);
                createUser("Marta", "Moderatrice", "moderator1@test.com", Role.MODERATOR, null, null, null);
                createUser("Lorenzo", "Support", "moderator2@test.com", Role.MODERATOR, null, null, null);
                createUser("Elisa", "Care", "moderator3@test.com", Role.MODERATOR, null, null, null);

                Plan[] plans = { basicS, basicA, premiumS, premiumA };
                PaymentFrequency[] freqs = { PaymentFrequency.UNICA_SOLUZIONE, PaymentFrequency.RATE_MENSILI };
                User[] clients = { c1, c2, c3, c4 };
                for (int i = 0; i < clients.length; i++) {
                        createSubscription(clients[i], plans[i % 4], freqs[i % 2]);
                }

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

                LocalDate start = LocalDate.now().plusDays(1);
                LocalDate end = start.plusDays(6);
                User[] pros = { pt1, pt2, nut1, nut2 };
                for (User pro : pros)
                        generateSlotsForProfessional(pro, start, end);

                bookSlot(slotRepository.findByProfessionalAndBookedByIsNull(pt1), 0, c1, pt1, true);
                bookSlot(slotRepository.findByProfessionalAndBookedByIsNull(pt1), 1, c2, pt1, true);
                bookSlot(slotRepository.findByProfessionalAndBookedByIsNull(pt2), 0, c3, pt2, true);
                bookSlot(slotRepository.findByProfessionalAndBookedByIsNull(pt2), 1, c4, pt2, true);
                bookSlot(slotRepository.findByProfessionalAndBookedByIsNull(nut1), 0, c1, nut1, false);
                bookSlot(slotRepository.findByProfessionalAndBookedByIsNull(nut1), 1, c3, nut1, false);
                bookSlot(slotRepository.findByProfessionalAndBookedByIsNull(nut2), 0, c2, nut2, false);
                bookSlot(slotRepository.findByProfessionalAndBookedByIsNull(nut2), 1, c4, nut2, false);

                LocalDateTime pastTime = LocalDateTime.now().minusDays(2).withHour(10).withMinute(0);
                Slot pastSlot = slotRepository.save(Slot.builder().professional(pt1)
                                .startTime(pastTime).endTime(pastTime.plusMinutes(30)).bookedBy(c4).build());

                String pastLink = "https://meet.jit.si/SkyAle_Consulto_" + c4.getId() + "_" + pt1.getId() + "_"
                                + UUID.randomUUID().toString().substring(0, 8);

                bookingRepository.save(Booking.builder().user(c4).professional(pt1).slot(pastSlot)
                                .meetingLink(pastLink).status(BookingStatus.COMPLETED).build());
        }

        private void dropLegacySlotIdUniqueConstraint() {
                try {
                        String sql = "DO $$ " +
                                     "DECLARE " +
                                     "    constraint_name text; " +
                                     "BEGIN " +
                                     "    SELECT conname INTO constraint_name " +
                                     "    FROM pg_constraint " +
                                     "    WHERE conrelid = 'bookings'::regclass AND contype = 'u' " +
                                     "    AND conkey = (SELECT array_agg(attnum) FROM pg_attribute WHERE attrelid = 'bookings'::regclass AND attname = 'slot_id'); " +
                                     "    " +
                                     "    IF constraint_name IS NOT NULL THEN " +
                                     "        EXECUTE 'ALTER TABLE bookings DROP CONSTRAINT ' || constraint_name; " +
                                     "    END IF; " +
                                     "END $$;";
                        jdbcTemplate.execute(sql);
                } catch (Exception ignored) {
                }
        }

        private void ensureUsersRoleCheckSupportsModerator() {
                String constraintName = "users_role_check";
                try {
                        jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS " + constraintName);
                        jdbcTemplate.execute(
                                        "ALTER TABLE users ADD CONSTRAINT " + constraintName
                                                        + " CHECK (role IN ('CLIENT','PERSONAL_TRAINER','NUTRITIONIST','MODERATOR','INSURANCE_MANAGER','ADMIN'))");
                } catch (Exception ignored) {
                }
        }

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

        @SuppressWarnings("unused")
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
                                                                slotRepository.save(Slot.builder()
                                                                                .professional(pro)
                                                                                .startTime(slotStart)
                                                                                .endTime(slotStart.plusMinutes(30))
                                                                                .build());
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
                slot.setBookedBy(client);
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

                bookingRepository.save(bookingDirector.buildConfirmedBooking(client, professional, slot, meetLink));
        }
}
