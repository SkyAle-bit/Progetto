package com.project.tesi.builder.impl;

import com.project.tesi.builder.BookingBuilder;
import com.project.tesi.enums.BookingStatus;
import com.project.tesi.model.Booking;
import com.project.tesi.model.Slot;
import com.project.tesi.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

public class BookingBuilderImpl implements BookingBuilder {

    private static final String JITSI_URL_REGEX = "^https://meet\\.jit\\.si/.+$";

    private Long id;
    private User user;
    private User professional;
    private Slot slot;
    private BookingStatus status;
    private String meetingLink;
    private boolean reminderSent = false;
    private LocalDateTime bookedAt;

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
    public BookingBuilder bookedAt(LocalDateTime bookedAt) {
        this.bookedAt = bookedAt;
        return this;
    }

    @Override
    public Booking build() {
        Objects.requireNonNull(this.user, "user è obbligatorio");
        Objects.requireNonNull(this.professional, "professional è obbligatorio");
        Objects.requireNonNull(this.slot, "slot è obbligatorio");
        Objects.requireNonNull(this.status, "status è obbligatorio");
        Objects.requireNonNull(this.meetingLink, "meetingLink è obbligatorio");
        if (!this.meetingLink.matches(JITSI_URL_REGEX))
            throw new IllegalArgumentException("meetingLink deve essere un URL Jitsi valido (https://meet.jit.si/...)");
        if (this.user.getId() != null && this.professional.getId() != null
                && this.user.getId().equals(this.professional.getId()))
            throw new IllegalStateException("user e professional non possono essere lo stesso utente");

        Booking obj = new Booking();
        obj.setId(this.id);
        obj.setUser(this.user);
        obj.setProfessional(this.professional);
        obj.setSlot(this.slot);
        obj.setStatus(this.status);
        obj.setMeetingLink(this.meetingLink);
        obj.setReminderSent(this.reminderSent);
        if (this.bookedAt != null) {
            obj.setBookedAt(this.bookedAt);
        }
        return obj;
    }
}
