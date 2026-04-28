package com.project.tesi.service.impl;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.Role;
import com.project.tesi.exception.booking.NoActiveSubscriptionException;
import com.project.tesi.exception.booking.SlotAlreadyBookedException;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.mapper.BookingMapper;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Slot;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.BookingRepository;
import com.project.tesi.repository.SlotRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.VideoConferenceService;
import com.project.tesi.service.strategy.BookingStrategy;
import com.project.tesi.service.strategy.PersonalTrainerBookingStrategy;
import com.project.tesi.observer.manager.EventManager;
import com.project.tesi.enums.EventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per {@link BookingServiceImpl}.
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private SlotRepository slotRepository;
    @Mock private UserRepository userRepository;
    @Mock private SubscriptionRepository subscriptionRepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private VideoConferenceService videoConferenceService;
    @Mock private EventManager eventManager;

    private BookingServiceImpl bookingService;

    private User client;
    private User pt;
    private Slot slot;
    private Subscription subscription;

    @BeforeEach
    void setUp() {
        pt = User.builder().email("pt@test.com").password("pass").role(Role.PERSONAL_TRAINER).id(2L).firstName("Luca").lastName("Bianchi").build();
        client = User.builder().email("mario@test.com").password("pass").role(Role.CLIENT).id(1L).firstName("Mario").lastName("Rossi").assignedPT(pt).build();

        slot = Slot.builder().id(10L).professional(pt)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .isBooked(false).build();

        com.project.tesi.model.Plan plan = com.project.tesi.model.Plan.builder().name("Plan").duration(com.project.tesi.enums.PlanDuration.ANNUALE).fullPrice(100.0).monthlyInstallmentPrice(10.0).build();
        subscription = Subscription.builder()
                .id(100L).user(client).plan(plan).paymentFrequency(com.project.tesi.enums.PaymentFrequency.UNICA_SOLUZIONE)
                .active(true).currentCreditsPT(5).currentCreditsNutri(3).endDate(LocalDateTime.now().plusMonths(1).toLocalDate()).build();

        // Crea strategy reale per PT
        BookingStrategy ptStrategy = new PersonalTrainerBookingStrategy();
        bookingService = new BookingServiceImpl(bookingRepository, slotRepository, userRepository,
                subscriptionRepository, bookingMapper, List.of(ptStrategy), videoConferenceService, eventManager);
    }

    @Test
    @DisplayName("createBooking — prenotazione riuscita con crediti scalati")
    void createBooking_success() {
        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setSlotId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(slotRepository.findById(10L)).thenReturn(Optional.of(slot));
        when(subscriptionRepository.findByUserAndActiveTrue(client)).thenReturn(Optional.of(subscription));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });

        when(videoConferenceService.generateMeetingLink(any(), any(), any())).thenReturn("https://meet.jit.si/test");

        BookingResponse expectedResp = BookingResponse.builder().id(1L).status(BookingStatus.CONFIRMED).build();
        when(bookingMapper.toResponse(any())).thenReturn(expectedResp);

        BookingResponse result = bookingService.createBooking(request);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(slot.isBooked()).isTrue();
        
        verify(bookingRepository).save(any(Booking.class));
        verify(eventManager).notifyListeners(eq(EventType.BOOKING_CREATED), any(Booking.class));
    }

    @Test
    @DisplayName("createBooking — utente non trovato lancia ResourceNotFoundException")
    void createBooking_userNotFound() {
        BookingRequest request = new BookingRequest();
        request.setUserId(999L);
        request.setSlotId(10L);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createBooking — slot non trovato lancia ResourceNotFoundException")
    void createBooking_slotNotFound() {
        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setSlotId(999L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(slotRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("createBooking — slot già prenotato lancia SlotAlreadyBookedException")
    void createBooking_slotAlreadyBooked() {
        slot.setBooked(true);
        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setSlotId(10L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(slotRepository.findById(10L)).thenReturn(Optional.of(slot));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(SlotAlreadyBookedException.class);
    }

    @Test
    @DisplayName("createBooking — nessun abbonamento attivo lancia NoActiveSubscriptionException")
    void createBooking_noSubscription() {
        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setSlotId(10L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(slotRepository.findById(10L)).thenReturn(Optional.of(slot));
        when(subscriptionRepository.findByUserAndActiveTrue(client)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(NoActiveSubscriptionException.class);
    }

    @Test
    @DisplayName("createBooking — ruolo professionista non supportato lancia IllegalStateException")
    void createBooking_unsupportedRole() {
        User admin = User.builder().email("admin@test.com").password("pass").role(Role.ADMIN).id(3L).build();
        Slot adminSlot = Slot.builder().id(20L).professional(admin).isBooked(false)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1)).build();

        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setSlotId(20L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(slotRepository.findById(20L)).thenReturn(Optional.of(adminSlot));

        assertThatThrownBy(() -> bookingService.createBooking(request))
                .isInstanceOf(IllegalStateException.class);
    }
}

