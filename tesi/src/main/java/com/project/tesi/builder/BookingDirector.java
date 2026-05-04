package com.project.tesi.builder;

import com.project.tesi.enums.BookingStatus;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Slot;
import com.project.tesi.model.User;

public class BookingDirector {

    private final BookingBuilder builder;

    public BookingDirector(BookingBuilder builder) {
        this.builder = builder;
    }

    public Booking buildConfirmedBooking(User user, User professional, Slot slot, String meetingLink) {
        return builder.user(user)
                .professional(professional)
                .slot(slot)
                .meetingLink(meetingLink)
                .status(BookingStatus.CONFIRMED)
                .build();
    }
}
