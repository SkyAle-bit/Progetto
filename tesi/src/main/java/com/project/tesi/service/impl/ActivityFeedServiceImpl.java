package com.project.tesi.service.impl;

import com.project.tesi.dto.response.ActivityFeedItemResponse;
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
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio per il feed delle attività recenti.
 */
@Service
public class ActivityFeedServiceImpl implements ActivityFeedService {

    private static final Logger log = LoggerFactory.getLogger(ActivityFeedServiceImpl.class);

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final DocumentRepository documentRepository;

    public ActivityFeedServiceImpl(UserRepository userRepository,
                                   BookingRepository bookingRepository,
                                   DocumentRepository documentRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.documentRepository = documentRepository;
    }

    private record SortableItem(ActivityFeedItemResponse item, LocalDateTime sortKey) {}

    @Override
    @Transactional(readOnly = true)
    public List<ActivityFeedItemResponse> getActivityFeed(Long userId, int days, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", userId));

        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<SortableItem> sortable = new ArrayList<>();

        if (user.getRole() == Role.CLIENT) {
            collectClientFeed(user, since, sortable);
        } else if (user.getRole() == Role.PERSONAL_TRAINER || user.getRole() == Role.NUTRITIONIST) {
            collectProfessionalFeed(user, since, sortable);
        }

        return sortable.stream()
                .sorted(Comparator.comparing(SortableItem::sortKey).reversed())
                .limit(limit)
                .map(SortableItem::item)
                .collect(Collectors.toList());
    }

    private void collectClientFeed(User client, LocalDateTime since, List<SortableItem> out) {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM", Locale.ITALIAN);
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        for (Booking b : bookingRepository.findRecentByUser(client, since)) {
            String proName = b.getProfessional().getFirstName();
            String proRole = b.getProfessional().getRole() == Role.PERSONAL_TRAINER ? "PT" : "Nutrizionista";
            String slotDate = b.getSlot().getStartTime().format(dateFmt);
            String slotTime = b.getSlot().getStartTime().format(timeFmt);
            out.add(toSortable("booking", "📅",
                    "Appuntamento prenotato con " + proRole + " " + proName + " per il " + slotDate + " alle " + slotTime,
                    b.getBookedAt()));
        }

        for (Document d : documentRepository.findRecentByOwner(client, since)) {
            String uploaderName = d.getUploadedBy() != null ? d.getUploadedBy().getFirstName() : "Sistema";
            out.add(toSortable("document", docIcon(d.getType()),
                    "Nuova " + getDocTypeLabel(d.getType()) + " caricata da " + uploaderName,
                    d.getUploadDate()));
        }
    }

    private void collectProfessionalFeed(User professional, LocalDateTime since, List<SortableItem> out) {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM", Locale.ITALIAN);
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        for (Booking b : bookingRepository.findRecentByProfessional(professional, since)) {
            String clientName = b.getUser().getFirstName() + " " + b.getUser().getLastName();
            String slotDate = b.getSlot().getStartTime().format(dateFmt);
            String slotTime = b.getSlot().getStartTime().format(timeFmt);
            out.add(toSortable("booking", "📅",
                    clientName + " ha prenotato un appuntamento per il " + slotDate + " alle " + slotTime,
                    b.getBookedAt()));
        }

        for (Document d : documentRepository.findRecentByUploader(professional, since)) {
            String clientName = d.getOwner() != null
                    ? d.getOwner().getFirstName() + " " + d.getOwner().getLastName() : "—";
            out.add(toSortable("document", docIcon(d.getType()),
                    getDocTypeLabel(d.getType()) + " caricata per " + clientName,
                    d.getUploadDate()));
        }
    }

    private SortableItem toSortable(String type, String icon, String text, LocalDateTime ts) {
        return new SortableItem(
                new ActivityFeedItemResponse(type, icon, text, ts.toString(), getTimeAgo(ts)),
                ts);
    }

    private String docIcon(DocumentType type) {
        return switch (type) {
            case WORKOUT_PLAN -> "💪";
            case DIET_PLAN -> "🥗";
            default -> "📄";
        };
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

    @Override
    @Transactional
    public void logBookingCreated(Booking booking) {
        if (booking.getBookedAt() == null) {
            booking.setBookedAt(LocalDateTime.now());
            bookingRepository.save(booking);
            log.info("ActivityFeed [Observer]: timestamp bookedAt registrato per prenotazione ID={}", booking.getId());
        } else {
            log.info("ActivityFeed [Observer]: prenotazione ID={} già registrata (bookedAt={}).",
                    booking.getId(), booking.getBookedAt());
        }
    }

    @Override
    public void logDocumentUploaded(Long clientId, Long uploaderId, String type) {
        log.info("ActivityFeed [Facade Orchestration]: upload documento tipo {} per clientId={} da uploaderId={}", type, clientId, uploaderId);
    }
}
