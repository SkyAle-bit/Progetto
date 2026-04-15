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
import com.project.tesi.service.ProfessionalStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementazione del servizio per le statistiche della dashboard del professionista.
 *
 * Calcola in sola lettura:
 * <ul>
 *   <li>Appuntamenti di oggi con dettagli cliente</li>
 *   <li>Numero totale di clienti assegnati</li>
 *   <li>Documenti caricati nella settimana corrente</li>
 *   <li>Clienti che necessitano di aggiornamenti (es. schede scadute)</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class ProfessionalStatsServiceImpl implements ProfessionalStatsService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final DocumentRepository documentRepository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProfessionalStats(Long professionalId) {
        User professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Professionista", professionalId));

        if (professional.getRole() != Role.PERSONAL_TRAINER && professional.getRole() != Role.NUTRITIONIST) {
            throw new IllegalArgumentException("L'utente con ID " + professionalId + " non è un professionista.");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();
        List<Booking> todayBookings = bookingRepository.findTodayByProfessional(professional, dayStart, dayEnd);

        List<Map<String, Object>> todayBookingsList = todayBookings.stream().map(b -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", b.getId());
            map.put("clientName", b.getUser().getFirstName() + " " + b.getUser().getLastName());
            map.put("clientId", b.getUser().getId());
            map.put("startTime", b.getSlot().getStartTime().toLocalTime().toString().substring(0, 5));
            map.put("endTime", b.getSlot().getEndTime().toLocalTime().toString().substring(0, 5));
            map.put("status", b.getStatus().name());
            map.put("meetingLink", b.getMeetingLink());
            return map;
        }).collect(Collectors.toList());

        List<User> clients;
        DocumentType relevantDocType;
        if (professional.getRole() == Role.PERSONAL_TRAINER) {
            clients = userRepository.findByAssignedPT(professional);
            relevantDocType = DocumentType.WORKOUT_PLAN;
        } else {
            clients = userRepository.findByAssignedNutritionist(professional);
            relevantDocType = DocumentType.DIET_PLAN;
        }

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Map<String, Object>> clientsNeedingAttention = new ArrayList<>();
        for (User client : clients) {
            Document latestDoc = documentRepository.findLatestByOwnerAndType(client, relevantDocType);
            boolean needsAttention = (latestDoc == null || latestDoc.getUploadDate().isBefore(sevenDaysAgo));
            if (needsAttention) {
                Map<String, Object> clientMap = new HashMap<>();
                clientMap.put("id", client.getId());
                clientMap.put("firstName", client.getFirstName());
                clientMap.put("lastName", client.getLastName());
                clientMap.put("lastDocDate", latestDoc != null ? latestDoc.getUploadDate().toString() : null);
                clientMap.put("daysSinceLastDoc", latestDoc != null
                        ? java.time.Duration.between(latestDoc.getUploadDate(), LocalDateTime.now()).toDays()
                        : -1);
                clientsNeedingAttention.add(clientMap);
            }
        }

        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime weekStart = startOfWeek.atStartOfDay();
        int docsUploadedThisWeek = documentRepository.countByUploaderSince(professional, weekStart);

        Map<String, Object> stats = new HashMap<>();
        stats.put("todayBookings", todayBookingsList);
        stats.put("todayBookingsCount", todayBookingsList.size());
        stats.put("clientsNeedingAttention", clientsNeedingAttention);
        stats.put("clientsNeedingAttentionCount", clientsNeedingAttention.size());
        stats.put("docsUploadedThisWeek", docsUploadedThisWeek);
        stats.put("totalClients", clients.size());

        return stats;
    }
}

