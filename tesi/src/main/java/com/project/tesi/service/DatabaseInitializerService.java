package com.project.tesi.service;

import com.project.tesi.enums.*;
import com.project.tesi.model.*;
import com.project.tesi.repository.*;
import jakarta.persistence.EntityManager;
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
        private final ChatMessageRepository chatMessageRepository;
        private final DocumentRepository documentRepository;
        private final PasswordEncoder passwordEncoder;
        private final EntityManager entityManager;

        @Transactional
        public void initialize() {
                // Svuota tutte le tabelle nell'ordine corretto (rispetta le FK)
                chatMessageRepository.deleteAllInBatch();
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
                chatMessageRepository.save(ChatMessage.builder()
                                .sender(sender)
                                .receiver(receiver)
                                .content(content)
                                .build());
        }
}

