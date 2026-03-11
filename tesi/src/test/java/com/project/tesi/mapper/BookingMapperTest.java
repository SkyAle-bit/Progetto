package com.project.tesi.mapper;

import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.Role;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Slot;
import com.project.tesi.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitari per {@link BookingMapper}.
 */
class BookingMapperTest {

    private final BookingMapper mapper = new BookingMapper();

    @Test
    @DisplayName("toResponse — converte correttamente un Booking nel DTO")
    void toResponse_success() {
        User client = User.builder().id(1L).firstName("Mario").lastName("Rossi").build();
        User pt = User.builder().id(2L).firstName("Luca").lastName("Bianchi").role(Role.PERSONAL_TRAINER).build();

        LocalDateTime start = LocalDateTime.of(2026, 3, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 3, 15, 11, 0);
        Slot slot = Slot.builder().startTime(start).endTime(end).build();

        Booking booking = Booking.builder()
                .id(1L).user(client).professional(pt).slot(slot)
                .meetingLink("https://meet.jit.si/test")
                .status(BookingStatus.CONFIRMED).build();

        BookingResponse response = mapper.toResponse(booking);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDate()).isEqualTo("2026-03-15");
        assertThat(response.getStartTime()).isEqualTo("10:00");
        assertThat(response.getEndTime()).isEqualTo("11:00");
        assertThat(response.getProfessionalName()).isEqualTo("Luca Bianchi");
        assertThat(response.getClientName()).isEqualTo("Mario Rossi");
        assertThat(response.getProfessionalRole()).isEqualTo(Role.PERSONAL_TRAINER);
        assertThat(response.getMeetingLink()).isEqualTo("https://meet.jit.si/test");
        assertThat(response.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(response.isCanJoin()).isTrue();
    }

    @Test
    @DisplayName("toResponse — null input restituisce null")
    void toResponse_null() {
        assertThat(mapper.toResponse(null)).isNull();
    }
}

