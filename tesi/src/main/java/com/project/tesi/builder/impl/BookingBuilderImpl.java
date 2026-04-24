package com.project.tesi.builder.impl;

import com.project.tesi.builder.BookingBuilder;
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


public class BookingBuilderImpl implements BookingBuilder {
    private Long id;
    private User user;
    private User professional;
    private Slot slot;
    private BookingStatus status;
    private LocalDateTime bookedAt;
    private String meetingLink;
    private boolean reminderSent= false;

    @Override
    public BookingBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public BookingBuilder user(User user) {
        this.user = user;
        return this;
    }
    @Override
    public BookingBuilder professional(User professional) {
        this.professional = professional;
        return this;
    }
    @Override
    public BookingBuilder slot(Slot slot) {
        this.slot = slot;
        return this;
    }
    @Override
    public BookingBuilder status(BookingStatus status) {
        this.status = status;
        return this;
    }
    @Override
    public BookingBuilder bookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
        return this;
    }
    @Override
    public BookingBuilder meetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
        return this;
    }
    @Override
    public BookingBuilder reminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
        return this;
    }

    @Override
    public Booking build() {
        Booking obj = new Booking();
        obj.setId(this.id);
        obj.setUser(this.user);
        obj.setProfessional(this.professional);
        obj.setSlot(this.slot);
        obj.setStatus(this.status);
        obj.setBookedAt(this.bookedAt);
        obj.setMeetingLink(this.meetingLink);
        obj.setReminderSent(this.reminderSent);
        return obj;
    }
}
