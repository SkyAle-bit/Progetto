package com.project.tesi.scheduler;

import com.project.tesi.model.Booking;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Cron job che gira ogni 5 minuti in background.
 * Spazzola il database in cerca di appuntamenti imminenti (entro i prossimi 35 minuti) 
 * e spara una mail di reminder col link della call a cliente e professionista.
 */
@Component
public class BookingReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(BookingReminderScheduler.class);

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public BookingReminderScheduler(BookingRepository bookingRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "${schedule.time.bookings}")
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

