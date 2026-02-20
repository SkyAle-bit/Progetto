package com.project.tesi.mapper;

import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Slot;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BookingMapper {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BookingResponse toResponse(Booking booking) {
        if (booking == null) return null;

        Slot slot = booking.getSlot();
        LocalDateTime start = slot.getStartTime();
        LocalDateTime end = slot.getEndTime();

        return BookingResponse.builder()
                .id(booking.getId())
                .date(start.format(DATE_FORMATTER))
                .startTime(start.format(TIME_FORMATTER))
                .endTime(end.format(TIME_FORMATTER))
                .professionalName(booking.getProfessional().getFirstName() + " " + booking.getProfessional().getLastName())
                .clientName(booking.getUser().getFirstName() + " " + booking.getUser().getLastName())
                .professionalRole(booking.getProfessional().getRole())
                .meetingLink(booking.getMeetingLink())
                .status(booking.getStatus())
                .canJoin(isMeetingJoinable(start))
                .build();
    }

    private boolean isMeetingJoinable(LocalDateTime startTime) {
        return LocalDateTime.now().isAfter(startTime.minusMinutes(10));
    }
}