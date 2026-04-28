package com.project.tesi.service.impl;

import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.DocumentType;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.model.*;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.DocumentRepository;
import com.project.tesi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfessionalStatsServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private DocumentRepository documentRepository;

    @InjectMocks private ProfessionalStatsServiceImpl statsService;

    private User pt, nutri, client;

    @BeforeEach
    void setUp() {
        pt = User.builder().email("pt@test.com").password("pass").role(Role.PERSONAL_TRAINER).id(2L).firstName("Luca").lastName("Bianchi").build();
        nutri = User.builder().email("nutri@test.com").password("pass").role(Role.NUTRITIONIST).id(3L).firstName("Sara").lastName("Verdi").build();
        client = User.builder().email("mario@test.com").password("pass").role(Role.CLIENT).id(1L).firstName("Mario").lastName("Rossi").build();
    }

    @Test @DisplayName("getProfessionalStats — PT con booking e clienti")
    void getProfessionalStats_pt() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));

        Slot slot = Slot.builder().professional(pt)
                .startTime(LocalDateTime.now().withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().withHour(10).withMinute(30)).build();
        Booking booking = Booking.builder().id(1L).user(client).professional(pt).slot(slot)
                .status(BookingStatus.CONFIRMED).meetingLink("https://meet.jit.si/test").build();
        when(bookingRepository.findTodayByProfessional(eq(pt), any(), any())).thenReturn(List.of(booking));
        when(userRepository.findByAssignedPT(pt)).thenReturn(List.of(client));
        // Documento vecchio → clienti che necessitano attenzione
        Document oldDoc = Document.builder().uploadDate(LocalDateTime.now().minusDays(10)).build();
        when(documentRepository.findLatestByOwnerAndType(client, DocumentType.WORKOUT_PLAN)).thenReturn(oldDoc);
        when(documentRepository.countByUploaderSince(eq(pt), any())).thenReturn(3);

        Map<String, Object> stats = statsService.getProfessionalStats(2L);

        assertThat(stats.get("todayBookingsCount")).isEqualTo(1);
        assertThat(stats.get("totalClients")).isEqualTo(1);
        assertThat(stats.get("docsUploadedThisWeek")).isEqualTo(3);
        assertThat(stats.get("clientsNeedingAttentionCount")).isEqualTo(1);
    }

    @Test @DisplayName("getProfessionalStats — Nutrizionista senza prenotazioni")
    void getProfessionalStats_nutri() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(nutri));
        when(bookingRepository.findTodayByProfessional(eq(nutri), any(), any())).thenReturn(List.of());
        when(userRepository.findByAssignedNutritionist(nutri)).thenReturn(List.of(client));
        // Nessun documento → necessita attenzione
        when(documentRepository.findLatestByOwnerAndType(client, DocumentType.DIET_PLAN)).thenReturn(null);
        when(documentRepository.countByUploaderSince(eq(nutri), any())).thenReturn(0);

        Map<String, Object> stats = statsService.getProfessionalStats(3L);

        assertThat(stats.get("todayBookingsCount")).isEqualTo(0);
        assertThat(stats.get("clientsNeedingAttentionCount")).isEqualTo(1);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> attention = (List<Map<String, Object>>) stats.get("clientsNeedingAttention");
        assertThat(attention.get(0).get("daysSinceLastDoc")).isEqualTo(-1L);
    }

    @Test @DisplayName("getProfessionalStats — professionista non trovato")
    void getProfessionalStats_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> statsService.getProfessionalStats(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("getProfessionalStats — utente non professionista lancia IllegalArgument")
    void getProfessionalStats_notProfessional() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        assertThatThrownBy(() -> statsService.getProfessionalStats(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test @DisplayName("getProfessionalStats — PT con documento recente → no attenzione necessaria")
    void getProfessionalStats_recentDoc() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(bookingRepository.findTodayByProfessional(eq(pt), any(), any())).thenReturn(List.of());
        when(userRepository.findByAssignedPT(pt)).thenReturn(List.of(client));
        // Documento recente → non necessita attenzione
        Document recentDoc = Document.builder().uploadDate(LocalDateTime.now().minusDays(2)).build();
        when(documentRepository.findLatestByOwnerAndType(client, DocumentType.WORKOUT_PLAN)).thenReturn(recentDoc);
        when(documentRepository.countByUploaderSince(eq(pt), any())).thenReturn(1);

        Map<String, Object> stats = statsService.getProfessionalStats(2L);
        assertThat(stats.get("clientsNeedingAttentionCount")).isEqualTo(0);
    }
}

