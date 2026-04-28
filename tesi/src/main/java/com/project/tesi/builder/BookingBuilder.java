package com.project.tesi.builder;

import com.project.tesi.enums.BookingStatus;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public interface BookingBuilder {
    BookingBuilder id(Long id);
    BookingBuilder user(User user);
    BookingBuilder professional(User professional);
    BookingBuilder slot(Slot slot);
    BookingBuilder status(BookingStatus status);
    BookingBuilder meetingLink(String meetingLink);
    BookingBuilder reminderSent(boolean reminderSent);
    BookingBuilder bookedAt(LocalDateTime bookedAt);
    Booking build();
}
