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
class ActivityFeedServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private DocumentRepository documentRepository;

    @InjectMocks private ActivityFeedServiceImpl activityFeedService;

    private User client, pt, nutri;

    @BeforeEach
    void setUp() {
        pt = User.builder().id(2L).firstName("Luca").lastName("Bianchi").role(Role.PERSONAL_TRAINER).build();
        nutri = User.builder().id(3L).firstName("Sara").lastName("Verdi").role(Role.NUTRITIONIST).build();
        client = User.builder().id(1L).firstName("Mario").lastName("Rossi").role(Role.CLIENT).build();
    }

    @Test @DisplayName("getActivityFeed — CLIENT con prenotazioni e documenti")
    void getActivityFeed_client() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));

        Slot slot = Slot.builder().startTime(LocalDateTime.now().plusDays(1)).endTime(LocalDateTime.now().plusDays(1).plusMinutes(30)).build();
        Booking booking = Booking.builder().id(1L).user(client).professional(pt).slot(slot)
                .status(BookingStatus.CONFIRMED).bookedAt(LocalDateTime.now().minusHours(2)).build();
        when(bookingRepository.findRecentByUser(eq(client), any())).thenReturn(List.of(booking));

        Document doc = Document.builder().id(1L).fileName("scheda.pdf").type(DocumentType.WORKOUT_PLAN)
                .owner(client).uploadedBy(pt).uploadDate(LocalDateTime.now().minusHours(1)).build();
        when(documentRepository.findRecentByOwner(eq(client), any())).thenReturn(List.of(doc));

        List<Map<String, Object>> result = activityFeedService.getActivityFeed(1L, 14, 15);
        assertThat(result).hasSize(2);
        // Il documento è più recente, deve essere primo
        assertThat(result.get(0).get("type")).isEqualTo("document");
        assertThat(result.get(1).get("type")).isEqualTo("booking");
    }

    @Test @DisplayName("getActivityFeed — professionista con prenotazioni e documenti")
    void getActivityFeed_professional() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));

        Slot slot = Slot.builder().startTime(LocalDateTime.now().plusDays(1)).endTime(LocalDateTime.now().plusDays(1).plusMinutes(30)).build();
        Booking booking = Booking.builder().id(1L).user(client).professional(pt).slot(slot)
                .bookedAt(LocalDateTime.now().minusMinutes(30)).build();
        when(bookingRepository.findRecentByProfessional(eq(pt), any())).thenReturn(List.of(booking));

        Document doc = Document.builder().id(1L).fileName("dieta.pdf").type(DocumentType.DIET_PLAN)
                .owner(client).uploadedBy(pt).uploadDate(LocalDateTime.now().minusMinutes(10)).build();
        when(documentRepository.findRecentByUploader(eq(pt), any())).thenReturn(List.of(doc));

        List<Map<String, Object>> result = activityFeedService.getActivityFeed(2L, 14, 15);
        assertThat(result).hasSize(2);
    }

    @Test @DisplayName("getActivityFeed — utente non trovato")
    void getActivityFeed_notFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> activityFeedService.getActivityFeed(999L, 14, 15))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("getActivityFeed — CLIENT senza attività restituisce lista vuota")
    void getActivityFeed_empty() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(bookingRepository.findRecentByUser(eq(client), any())).thenReturn(List.of());
        when(documentRepository.findRecentByOwner(eq(client), any())).thenReturn(List.of());

        List<Map<String, Object>> result = activityFeedService.getActivityFeed(1L, 14, 15);
        assertThat(result).isEmpty();
    }

    @Test @DisplayName("getActivityFeed — limit funziona correttamente")
    void getActivityFeed_limit() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));

        Slot slot = Slot.builder().startTime(LocalDateTime.now().plusDays(1)).endTime(LocalDateTime.now().plusDays(1).plusMinutes(30)).build();
        Booking b1 = Booking.builder().id(1L).user(client).professional(pt).slot(slot).bookedAt(LocalDateTime.now().minusHours(1)).build();
        Booking b2 = Booking.builder().id(2L).user(client).professional(pt).slot(slot).bookedAt(LocalDateTime.now().minusHours(2)).build();
        when(bookingRepository.findRecentByUser(eq(client), any())).thenReturn(List.of(b1, b2));
        when(documentRepository.findRecentByOwner(eq(client), any())).thenReturn(List.of());

        List<Map<String, Object>> result = activityFeedService.getActivityFeed(1L, 14, 1);
        assertThat(result).hasSize(1); // limitato a 1
    }

    @Test @DisplayName("getActivityFeed — documenti INSURANCE_POLICE e MEDICAL_CERT")
    void getActivityFeed_otherDocTypes() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(bookingRepository.findRecentByUser(eq(client), any())).thenReturn(List.of());

        Document police = Document.builder().id(1L).type(DocumentType.INSURANCE_POLICE)
                .owner(client).uploadedBy(null).uploadDate(LocalDateTime.now().minusHours(1)).build();
        Document cert = Document.builder().id(2L).type(DocumentType.MEDICAL_CERT)
                .owner(client).uploadedBy(pt).uploadDate(LocalDateTime.now().minusMinutes(30)).build();
        when(documentRepository.findRecentByOwner(eq(client), any())).thenReturn(List.of(police, cert));

        List<Map<String, Object>> result = activityFeedService.getActivityFeed(1L, 14, 15);
        assertThat(result).hasSize(2);
    }

    @Test @DisplayName("getActivityFeed — professionista con documento senza owner")
    void getActivityFeed_professionalDocNullOwner() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(pt));
        when(bookingRepository.findRecentByProfessional(eq(pt), any())).thenReturn(List.of());

        Document doc = Document.builder().id(1L).type(DocumentType.WORKOUT_PLAN)
                .owner(null).uploadedBy(pt).uploadDate(LocalDateTime.now().minusMinutes(5)).build();
        when(documentRepository.findRecentByUploader(eq(pt), any())).thenReturn(List.of(doc));

        List<Map<String, Object>> result = activityFeedService.getActivityFeed(2L, 14, 15);
        assertThat(result).hasSize(1);
        assertThat(((String) result.get(0).get("text"))).contains("—");
    }

    @Test @DisplayName("getActivityFeed — ADMIN restituisce lista vuota (nessun feed)")
    void getActivityFeed_admin() {
        User admin = User.builder().id(99L).role(Role.ADMIN).build();
        when(userRepository.findById(99L)).thenReturn(Optional.of(admin));

        List<Map<String, Object>> result = activityFeedService.getActivityFeed(99L, 14, 15);
        assertThat(result).isEmpty();
    }
}

