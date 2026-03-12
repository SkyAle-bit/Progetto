package com.project.tesi.scheduler;

import com.project.tesi.model.Booking;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler per l'invio automatico dei promemoria email
 * 30 minuti prima di ogni appuntamento confermato.
 *
 * Viene eseguito ogni 5 minuti (cron: {@code 0 *&#47;5 * * * ?}).
 * Cerca le prenotazioni CONFIRMED il cui slot inizia entro i prossimi 35 minuti
 * e che non hanno ancora ricevuto il promemoria, poi invia l'email sia
 * al cliente che al professionista.
 */
@Component
@RequiredArgsConstructor
public class BookingReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingReminderScheduler.class);

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    /**
     * Ogni 5 minuti cerca prenotazioni imminenti (entro 35 min) e invia
     * l'email di promemoria a cliente e professionista.
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional
    public void sendBookingReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowEnd = now.plusMinutes(35);

        List<Booking> upcoming = bookingRepository.findUpcomingNeedingReminder(now, windowEnd);

        if (upcoming.isEmpty()) return;

        log.info("Trovate {} prenotazioni imminenti da notificare", upcoming.size());

        for (Booking booking : upcoming) {
            try {
                User client = booking.getUser();
                User professional = booking.getProfessional();
                LocalDateTime startTime = booking.getSlot().getStartTime();
                String meetingLink = booking.getMeetingLink();

                String clientName = client.getFirstName() + " " + client.getLastName();
                String profName = professional.getFirstName() + " " + professional.getLastName();

                // Email al cliente
                emailService.sendBookingReminderEmail(
                        client.getEmail(),
                        client.getFirstName(),
                        profName,
                        startTime,
                        meetingLink,
                        true
                );

                // Email al professionista
                emailService.sendBookingReminderEmail(
                        professional.getEmail(),
                        professional.getFirstName(),
                        clientName,
                        startTime,
                        meetingLink,
                        false
                );

                // Segna come inviato per non re-inviare
                booking.setReminderSent(true);
                bookingRepository.save(booking);

                log.info("Promemoria inviato per booking #{} — {} con {}",
                        booking.getId(), clientName, profName);

            } catch (Exception e) {
                log.error("Errore nell'invio del promemoria per booking #{}: {}",
                        booking.getId(), e.getMessage());
            }
        }
    }
}

