package com.project.tesi.builder;

import com.project.tesi.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public interface BookingBuilder {
    BookingBuilder id(Long id);
    BookingBuilder user(User user);
    BookingBuilder professional(User professional);
    BookingBuilder slot(Slot slot);
    BookingBuilder status(BookingStatus status);
    BookingBuilder bookedAt(LocalDateTime bookedAt);
    BookingBuilder meetingLink(String meetingLink);
    BookingBuilder reminderSent(boolean reminderSent);
    Booking build();
}
