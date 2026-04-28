package com.project.tesi.builder.impl;

import com.project.tesi.builder.BookingBuilder;
import com.project.tesi.enums.BookingStatus;
import java.util.Objects;
import com.project.tesi.model.*;


public class BookingBuilderImpl implements BookingBuilder {
    private User user;
    private User professional;
    private Slot slot;
    private BookingStatus status;
    private String meetingLink;
    private boolean reminderSent= false;

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
    public Booking build() {
        Objects.requireNonNull(this.user, "user è obbligatorio");
        Objects.requireNonNull(this.slot, "slot è obbligatorio");
        Objects.requireNonNull(this.status, "status è obbligatorio");
        Objects.requireNonNull(this.meetingLink, "meetingLink è obbligatorio");

        Booking obj = new Booking();
        obj.setUser(this.user);
        obj.setProfessional(this.professional);
        obj.setSlot(this.slot);
        obj.setStatus(this.status);
        obj.setMeetingLink(this.meetingLink);
        obj.setReminderSent(this.reminderSent);
        return obj;
    }
}
