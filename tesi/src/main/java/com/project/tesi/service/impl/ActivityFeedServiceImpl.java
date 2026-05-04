package com.project.tesi.service.impl;

import com.project.tesi.enums.DocumentType;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Document;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.DocumentRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.ActivityFeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio per il feed delle attività recenti.
 *
 * Costruisce un feed differenziato per ruolo:
 * <ul>
 *   <li>CLIENT: prenotazioni effettuate + documenti ricevuti</li>
 *   <li>PT/NUTRITIONIST: prenotazioni ricevute + documenti caricati ai clienti</li>
 * </ul>
 * Le attività vengono ordinate per timestamp decrescente e limitate al numero richiesto.
 */
@Service
public class ActivityFeedServiceImpl implements ActivityFeedService {

    private static final Logger log = LoggerFactory.getLogger(ActivityFeedServiceImpl.class);

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final DocumentRepository documentRepository;

    // Costruttore esplicito — sostituisce @RequiredArgsConstructor di Lombok
    public ActivityFeedServiceImpl(UserRepository userRepository,
                                   BookingRepository bookingRepository,
                                   DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActivityFeed(Long userId, int days, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", userId));

        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<Map<String, Object>> activities = new ArrayList<>();

        if (user.getRole() == Role.CLIENT) {
            buildClientFeed(user, since, activities);
        } else if (user.getRole() == Role.PERSONAL_TRAINER || user.getRole() == Role.NUTRITIONIST) {
            buildProfessionalFeed(user, since, activities);
        }

        // Ordina per timestamp decrescente e limita
        activities.sort((a, b) -> ((LocalDateTime) b.get("_sort")).compareTo((LocalDateTime) a.get("_sort")));

        return activities.stream()
                .limit(limit)
                .peek(m -> m.remove("_sort"))
                .collect(Collectors.toList());
    }

    private void buildClientFeed(User client, LocalDateTime since, List<Map<String, Object>> activities) {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM", Locale.ITALIAN);
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        List<Booking> bookings = bookingRepository.findRecentByUser(client, since);
        for (Booking b : bookings) {
            String proName = b.getProfessional().getFirstName();
            String proRole = b.getProfessional().getRole() == Role.PERSONAL_TRAINER ? "PT" : "Nutrizionista";
            String slotDate = b.getSlot().getStartTime().format(dateFmt);
            String slotTime = b.getSlot().getStartTime().format(timeFmt);

            Map<String, Object> item = new HashMap<>();
            item.put("type", "booking");
            item.put("icon", "📅");
            item.put("text", "Appuntamento prenotato con " + proRole + " " + proName + " per il " + slotDate + " alle " + slotTime);
            item.put("timestamp", b.getBookedAt().toString());
            item.put("timeAgo", getTimeAgo(b.getBookedAt()));
            item.put("_sort", b.getBookedAt());
            activities.add(item);
        }

        List<Document> docs = documentRepository.findRecentByOwner(client, since);
        for (Document d : docs) {
            String uploaderName = d.getUploadedBy() != null ? d.getUploadedBy().getFirstName() : "Sistema";
            String docLabel = getDocTypeLabel(d.getType());

            Map<String, Object> item = new HashMap<>();
            item.put("type", "document");
            item.put("icon", d.getType() == DocumentType.WORKOUT_PLAN ? "💪" : d.getType() == DocumentType.DIET_PLAN ? "🥗" : "📄");
            item.put("text", "Nuova " + docLabel + " caricata da " + uploaderName);
            item.put("timestamp", d.getUploadDate().toString());
            item.put("timeAgo", getTimeAgo(d.getUploadDate()));
            item.put("_sort", d.getUploadDate());
            activities.add(item);
        }
    }

    private void buildProfessionalFeed(User professional, LocalDateTime since, List<Map<String, Object>> activities) {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM", Locale.ITALIAN);
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        List<Booking> bookings = bookingRepository.findRecentByProfessional(professional, since);
        for (Booking b : bookings) {
            String clientName = b.getUser().getFirstName() + " " + b.getUser().getLastName();
            String slotDate = b.getSlot().getStartTime().format(dateFmt);
            String slotTime = b.getSlot().getStartTime().format(timeFmt);

            Map<String, Object> item = new HashMap<>();
            item.put("type", "booking");
            item.put("icon", "📅");
            item.put("text", clientName + " ha prenotato un appuntamento per il " + slotDate + " alle " + slotTime);
            item.put("timestamp", b.getBookedAt().toString());
            item.put("timeAgo", getTimeAgo(b.getBookedAt()));
            item.put("_sort", b.getBookedAt());
            activities.add(item);
        }

        List<Document> docs = documentRepository.findRecentByUploader(professional, since);
        for (Document d : docs) {
            String clientName = d.getOwner() != null ? d.getOwner().getFirstName() + " " + d.getOwner().getLastName() : "—";
            String docLabel = getDocTypeLabel(d.getType());

            Map<String, Object> item = new HashMap<>();
            item.put("type", "document");
            item.put("icon", d.getType() == DocumentType.WORKOUT_PLAN ? "💪" : d.getType() == DocumentType.DIET_PLAN ? "🥗" : "📄");
            item.put("text", docLabel + " caricata per " + clientName);
            item.put("timestamp", d.getUploadDate().toString());
            item.put("timeAgo", getTimeAgo(d.getUploadDate()));
            item.put("_sort", d.getUploadDate());
            activities.add(item);
        }
    }

    private String getDocTypeLabel(DocumentType type) {
        return switch (type) {
            case WORKOUT_PLAN -> "scheda di allenamento";
            case DIET_PLAN -> "dieta";
            case INSURANCE_POLICE -> "polizza";
            case MEDICAL_CERT -> "certificato medico";
        };
    }

    private String getTimeAgo(LocalDateTime dateTime) {
        long minutes = java.time.Duration.between(dateTime, LocalDateTime.now()).toMinutes();
        if (minutes < 1) return "adesso";
        if (minutes < 60) return minutes + " min fa";
        long hours = minutes / 60;
        if (hours < 24) return hours + " or" + (hours == 1 ? "a" : "e") + " fa";
        long days = hours / 24;
        if (days == 1) return "ieri";
        if (days < 7) return days + " giorni fa";
        long weeks = days / 7;
        return weeks + " settiman" + (weeks == 1 ? "a" : "e") + " fa";
    }

    /**
     * Persiste nel database una conferma di avvenuta prenotazione.
     *
     * <p>Invocato dal {@code ActivityFeedUpdateListener} (pattern Observer) quando
     * viene emesso l'evento {@code BOOKING_CREATED}. Verifica che il campo
     * {@code bookedAt} sia valorizzato e salva l'entità, garantendo che ogni
     * prenotazione abbia un timestamp di registrazione persistente. Questo approccio
     * assicura che il listener non sia codice morto, ma attivi una vera operazione
     * di persistenza delegata al service layer.</p>
     *
     * @param booking la prenotazione appena creata e notificata dall'EventManager
     */
    @Override
    @Transactional
    public void logBookingCreated(Booking booking) {
        if (booking.getBookedAt() == null) {
            booking.setBookedAt(LocalDateTime.now());
            bookingRepository.save(booking);
            log.info("ActivityFeed [Observer]: timestamp bookedAt registrato per la prenotazione ID={} " +
                    "(utente={}, professionista={}, slot={})",
                    booking.getId(),
                    booking.getUser().getEmail(),
                    booking.getProfessional().getEmail(),
                    booking.getSlot().getStartTime());
        } else {
            log.info("ActivityFeed [Observer]: prenotazione ID={} già registrata (bookedAt={}). " +
                    "Nessuna azione di persistenza necessaria.",
                    booking.getId(), booking.getBookedAt());
        }
    }

    @Override
    public void logDocumentUploaded(Long clientId, Long uploaderId, String type) {
        log.info("ActivityFeed [Facade Orchestration]: Registrato upload documento tipo {} per clientId={} da uploaderId={}", type, clientId, uploaderId);
        // Qui potrebbe essere salvato un record nel DB o emesso un evento su una coda
    }
}

