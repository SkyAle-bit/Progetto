package com.project.tesi.builder;

import com.project.tesi.enums.BookingStatus;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Slot;
import com.project.tesi.model.User;
import org.springframework.stereotype.Component;

@Component
public class BookingDirector {

    public Booking buildConfirmedBooking(User user, User professional, Slot slot, String meetingLink) {
        return Booking.builder()
                .user(user)
                .professional(professional)
                .slot(slot)
                .meetingLink(meetingLink)
                .status(BookingStatus.CONFIRMED)
                .build();
    }
}
