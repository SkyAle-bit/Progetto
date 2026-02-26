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

                Plan basicS = planRepository.findByName("Basic Pack Semestrale").orElseThrow();
                Plan basicA = planRepository.findByName("Basic Pack Annuale").orElseThrow();
                Plan premiumS = planRepository.findByName("Premium Pack Semestrale").orElseThrow();
                Plan premiumA = planRepository.findByName("Premium Pack Annuale").orElseThrow();

                // 2. Personal Trainer (10)
                User pt1 = createUser("Marco", "Rossi", "pt1@test.com", Role.PERSONAL_TRAINER,
                                "Specializzato in allenamento funzionale e riabilitazione.", null, null);
                User pt2 = createUser("Giulia", "Bianchi", "pt2@test.com", Role.PERSONAL_TRAINER,
                                "Esperta in powerlifting e preparazione atletica.", null, null);
                User pt3 = createUser("Davide", "Russo", "pt3@test.com", Role.PERSONAL_TRAINER,
                                "Allenatore certificato NASM, specializzato in crossfit e HIIT.", null, null);
                User pt4 = createUser("Valeria", "Mancini", "pt4@test.com", Role.PERSONAL_TRAINER,
                                "Specializzata in yoga terapeutico e pilates.", null, null);
                User pt5 = createUser("Riccardo", "Colombo", "pt5@test.com", Role.PERSONAL_TRAINER,
                                "Ex atleta professionista, specializzato in atletica leggera.", null, null);
                User pt6 = createUser("Serena", "Barbieri", "pt6@test.com", Role.PERSONAL_TRAINER,
                                "Personal trainer certificata ACE, esperta in body recomposition.", null, null);
                User pt7 = createUser("Claudio", "Ferretti", "pt7@test.com", Role.PERSONAL_TRAINER,
                                "Specializzato in allenamento per over 50 e fisioterapia preventiva.", null, null);
                User pt8 = createUser("Ilaria", "De Luca", "pt8@test.com", Role.PERSONAL_TRAINER,
                                "Coach di forza e condizionamento, esperta in calisthenics.", null, null);
                User pt9 = createUser("Fabio", "Martinelli", "pt9@test.com", Role.PERSONAL_TRAINER,
                                "Trainer per sport di squadra e performance agonistica.", null, null);
                User pt10 = createUser("Paola", "Genovese", "pt10@test.com", Role.PERSONAL_TRAINER,
                                "Specializzata in training posturale e lavoro con le donne in post-parto.", null, null);

                // 3. Nutrizionisti (10)
                User nut1 = createUser("Laura", "Verdi", "nutri1@test.com", Role.NUTRITIONIST,
                                "Biologa nutrizionista, esperta in dieta mediterranea e sportiva.", null, null);
                User nut2 = createUser("Andrea", "Esposito", "nutri2@test.com", Role.NUTRITIONIST,
                                "Specializzato in nutrizione clinica e intolleranze alimentari.", null, null);
                User nut3 = createUser("Federica", "Ricci", "nutri3@test.com", Role.NUTRITIONIST,
                                "Dietista con focus su nutrizione pediatrica e donna in gravidanza.", null, null);
                User nut4 = createUser("Lorenzo", "Ferrari", "nutri4@test.com", Role.NUTRITIONIST,
                                "Esperto in nutrizione sportiva e supplementazione.", null, null);
                User nut5 = createUser("Alessia", "Monti", "nutri5@test.com", Role.NUTRITIONIST,
                                "Nutrizionista specializzata in vegano/vegetarianismo e sostenibilità.", null, null);
                User nut6 = createUser("Emanuele", "Conti", "nutri6@test.com", Role.NUTRITIONIST,
                                "Nutrizionista con dottorato in metabolismo e patologie metaboliche.", null, null);
                User nut7 = createUser("Chiara", "Sala", "nutri7@test.com", Role.NUTRITIONIST,
                                "Esperta in disturbi dell'alimentazione e recupero peso.", null, null);
                User nut8 = createUser("Roberto", "Palumbo", "nutri8@test.com", Role.NUTRITIONIST,
                                "Specializzato in nutrizione anti-aging e longevità.", null, null);
                User nut9 = createUser("Giovanna", "Aquino", "nutri9@test.com", Role.NUTRITIONIST,
                                "Coach nutrizionale per sportivi agonisti e amatori.", null, null);
                User nut10 = createUser("Stefano", "Carbone", "nutri10@test.com", Role.NUTRITIONIST,
                                "Ricercatore nutrizionale, esperto in microbioma e salute intestinale.", null, null);

                // 4. Clienti (20)
                User c1 = createUser("Luca", "Ferri", "luca@test.com", Role.CLIENT, null, pt1, nut1);
                User c2 = createUser("Sofia", "Conti", "sofia@test.com", Role.CLIENT, null, pt1, nut2);
                User c3 = createUser("Matteo", "Galli", "matteo@test.com", Role.CLIENT, null, pt2, nut1);
                User c4 = createUser("Chiara", "Fontana", "chiara@test.com", Role.CLIENT, null, pt2, nut2);
                User c5 = createUser("Alessandro", "Moretti", "alessandro@test.com", Role.CLIENT, null, pt1, nut3);
                User c6 = createUser("Beatrice", "Romano", "beatrice@test.com", Role.CLIENT, null, pt3, nut3);
                User c7 = createUser("Simone", "Colombo", "simone@test.com", Role.CLIENT, null, pt3, nut4);
                User c8 = createUser("Elisa", "Martini", "elisa@test.com", Role.CLIENT, null, pt4, nut3);
                User c9 = createUser("Gabriele", "Costa", "gabriele@test.com", Role.CLIENT, null, pt4, nut4);
                User c10 = createUser("Francesca", "Greco", "francesca@test.com", Role.CLIENT, null, pt2, nut5);
                User c11 = createUser("Nicola", "Barbieri", "nicola@test.com", Role.CLIENT, null, pt3, nut6);
                User c12 = createUser("Valentina", "Coppola", "valentina@test.com", Role.CLIENT, null, pt4, nut1);
                User c13 = createUser("Emilio", "Leone", "emilio@test.com", Role.CLIENT, null, pt5, nut5);
                User c14 = createUser("Arianna", "Fabbri", "arianna@test.com", Role.CLIENT, null, pt5, nut6);
                User c15 = createUser("Diego", "Pellegrini", "diego@test.com", Role.CLIENT, null, pt6, nut7);
                User c16 = createUser("Irene", "Longo", "irene@test.com", Role.CLIENT, null, pt7, nut8);
                User c17 = createUser("Andrea", "Serra", "andrea@test.com", Role.CLIENT, null, pt8, nut9);
                User c18 = createUser("Laura", "Vitale", "laura@test.com", Role.CLIENT, null, pt9, nut10);
                User c19 = createUser("Marco", "Grasso", "marcoG@test.com", Role.CLIENT, null, pt10, nut9);
                User c20 = createUser("Giulia", "Amato", "giuliaA@test.com", Role.CLIENT, null, pt6, nut10);

                // 5. Abbonamenti
                Plan[] plans = { basicS, basicA, premiumS, premiumA };
                PaymentFrequency[] freqs = { PaymentFrequency.UNICA_SOLUZIONE, PaymentFrequency.RATE_MENSILI };
                User[] clients = { c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12, c13, c14, c15, c16, c17, c18, c19,
                                c20 };
                for (int i = 0; i < clients.length; i++) {
                        createSubscription(clients[i], plans[i % 4], freqs[i % 2]);
                }

                // 6. Recensioni (PT e nutrizionisti, rating variati 3-5)
                String[][] ptReviews = {
                                { "5", "Ottimo preparatore, molto attento!" }, { "4", "Bravo, ma a volte in ritardo." },
                                { "5", "Super professionale!" }, { "5", "Motivante e preparato." },
                                { "4", "Buono, consigliato." },
                                { "5", "Programmi eccellenti!" }, { "4", "Bravo ma i tempi di risposta sono lunghi." },
                                { "5", "Fantastica con il pilates!" }, { "5", "Top! Consigliatissima." },
                                { "4", "Ottimi risultati." },
                                { "3", "Nella media." }, { "5", "Eccezionale, mi ha cambiato l'approccio." },
                                { "4", "Professionale e puntuale." }, { "5", "Molto soddisfatto!" }, { "4", "Buono." },
                                { "5", "Consiglio a tutti!" }, { "4", "Preparato." }, { "5", "Brillante!" },
                                { "4", "Ottimo." },
                                { "5", "Fantastico!" }
                };
                String[][] nutReviews = {
                                { "5", "Piano alimentare perfetto." }, { "4", "Qualche ricetta poco chiara." },
                                { "5", "La dieta funziona alla grande!" }, { "5", "Disponibile e competente." },
                                { "4", "Soddisfatto, risultati visibili." }, { "5", "Piani super personalizzati." },
                                { "5", "Capisce la nutrizione atletica!" }, { "4", "Professionale e disponibile." },
                                { "5", "Supplementazione al meglio." }, { "3", "Qualche consiglio generico." },
                                { "4", "Piano chiaro e conciso." }, { "5", "Raccomando a tutti!" },
                                { "4", "Molto bravo." }, { "5", "Eccellente!" }, { "4", "Soddisfatta." },
                                { "5", "Ottima professionista." }, { "4", "Preparato." }, { "5", "Coach fantastica!" },
                                { "4", "Bravo." }, { "5", "Magnifico!" }
                };
                User[] ptAssigned = { pt1, pt1, pt2, pt2, pt1, pt3, pt3, pt4, pt4, pt2, pt3, pt4, pt5, pt5, pt6, pt7,
                                pt8, pt9,
                                pt10, pt6 };
                User[] nutAssigned = { nut1, nut2, nut1, nut2, nut3, nut3, nut4, nut3, nut4, nut5, nut6, nut1, nut5,
                                nut6, nut7,
                                nut8, nut9, nut10, nut9, nut10 };
                for (int i = 0; i < 20; i++) {
                        createReview(clients[i], ptAssigned[i], Integer.parseInt(ptReviews[i][0]), ptReviews[i][1]);
                        createReview(clients[i], nutAssigned[i], Integer.parseInt(nutReviews[i][0]), nutReviews[i][1]);
                }

                // 7. Orari settimanali
                // PT schedules
                createWeeklySchedule(pt1, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(pt1, DayOfWeek.WEDNESDAY, LocalTime.of(15, 0), LocalTime.of(19, 0));
                createWeeklySchedule(pt1, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(pt2, DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                createWeeklySchedule(pt2, DayOfWeek.THURSDAY, LocalTime.of(16, 0), LocalTime.of(20, 0));
                createWeeklySchedule(pt2, DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(12, 0));
                createWeeklySchedule(pt3, DayOfWeek.MONDAY, LocalTime.of(7, 0), LocalTime.of(11, 0));
                createWeeklySchedule(pt3, DayOfWeek.WEDNESDAY, LocalTime.of(12, 0), LocalTime.of(16, 0));
                createWeeklySchedule(pt3, DayOfWeek.FRIDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
                createWeeklySchedule(pt4, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
                createWeeklySchedule(pt4, DayOfWeek.THURSDAY, LocalTime.of(13, 0), LocalTime.of(17, 0));
                createWeeklySchedule(pt4, DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(pt5, DayOfWeek.MONDAY, LocalTime.of(6, 0), LocalTime.of(10, 0));
                createWeeklySchedule(pt5, DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                createWeeklySchedule(pt5, DayOfWeek.FRIDAY, LocalTime.of(16, 0), LocalTime.of(20, 0));
                createWeeklySchedule(pt6, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(pt6, DayOfWeek.THURSDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
                createWeeklySchedule(pt6, DayOfWeek.SATURDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
                createWeeklySchedule(pt7, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                createWeeklySchedule(pt7, DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
                createWeeklySchedule(pt7, DayOfWeek.FRIDAY, LocalTime.of(15, 0), LocalTime.of(18, 0));
                createWeeklySchedule(pt8, DayOfWeek.TUESDAY, LocalTime.of(7, 0), LocalTime.of(11, 0));
                createWeeklySchedule(pt8, DayOfWeek.THURSDAY, LocalTime.of(15, 0), LocalTime.of(19, 0));
                createWeeklySchedule(pt8, DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(13, 0));
                createWeeklySchedule(pt9, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
                createWeeklySchedule(pt9, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
                createWeeklySchedule(pt9, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(pt10, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(pt10, DayOfWeek.THURSDAY, LocalTime.of(15, 0), LocalTime.of(19, 0));
                createWeeklySchedule(pt10, DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                // Nutrizionisti schedules
                createWeeklySchedule(nut1, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
                createWeeklySchedule(nut1, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut1, DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                createWeeklySchedule(nut2, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut2, DayOfWeek.THURSDAY, LocalTime.of(15, 0), LocalTime.of(19, 0));
                createWeeklySchedule(nut2, DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut3, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
                createWeeklySchedule(nut3, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
                createWeeklySchedule(nut3, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(12, 0));
                createWeeklySchedule(nut4, DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                createWeeklySchedule(nut4, DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut4, DayOfWeek.SATURDAY, LocalTime.of(11, 0), LocalTime.of(14, 0));
                createWeeklySchedule(nut5, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                createWeeklySchedule(nut5, DayOfWeek.WEDNESDAY, LocalTime.of(15, 0), LocalTime.of(19, 0));
                createWeeklySchedule(nut5, DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
                createWeeklySchedule(nut6, DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(12, 0));
                createWeeklySchedule(nut6, DayOfWeek.THURSDAY, LocalTime.of(13, 0), LocalTime.of(17, 0));
                createWeeklySchedule(nut6, DayOfWeek.SATURDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut7, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut7, DayOfWeek.WEDNESDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
                createWeeklySchedule(nut7, DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(15, 0));
                createWeeklySchedule(nut8, DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(14, 0));
                createWeeklySchedule(nut8, DayOfWeek.THURSDAY, LocalTime.of(15, 0), LocalTime.of(19, 0));
                createWeeklySchedule(nut8, DayOfWeek.SATURDAY, LocalTime.of(8, 0), LocalTime.of(11, 0));
                createWeeklySchedule(nut9, DayOfWeek.MONDAY, LocalTime.of(7, 0), LocalTime.of(11, 0));
                createWeeklySchedule(nut9, DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(17, 0));
                createWeeklySchedule(nut9, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut10, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(13, 0));
                createWeeklySchedule(nut10, DayOfWeek.THURSDAY, LocalTime.of(14, 0), LocalTime.of(18, 0));
                createWeeklySchedule(nut10, DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(13, 0));

                // 8. Genera slot (prossimi 7 giorni)
                LocalDate start = LocalDate.now().plusDays(1);
                LocalDate end = start.plusDays(6);
                User[] pros = { pt1, pt2, pt3, pt4, pt5, pt6, pt7, pt8, pt9, pt10,
                                nut1, nut2, nut3, nut4, nut5, nut6, nut7, nut8, nut9, nut10 };
                for (User pro : pros)
                        generateSlotsForProfessional(pro, start, end);

                // 9. Prenotazioni campione
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(pt1), 0, c1, pt1, true);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(pt1), 0, c2, pt1, true);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(pt2), 0, c3, pt2, true);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(pt3), 0, c6, pt3, true);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(pt4), 0, c8, pt4, true);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(nut1), 0, c1, nut1, false);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(nut2), 0, c4, nut2, false);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(nut3), 0, c6, nut3, false);
                bookSlot(slotRepository.findByProfessionalAndIsBookedFalse(nut4), 0, c7, nut4, false);

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