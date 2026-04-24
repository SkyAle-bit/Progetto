package com.project.tesi.builder.impl;

import com.project.tesi.builder.SlotBuilder;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public class SlotBuilderImpl implements SlotBuilder {
    private Long id;
    private User professional;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isBooked;
    private Integer version;

    @Override
    public SlotBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public SlotBuilder professional(User professional) {
        this.professional = professional;
        return this;
    }
    @Override
    public SlotBuilder startTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }
    @Override
    public SlotBuilder endTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }
    @Override
    public SlotBuilder isBooked(boolean isBooked) {
        this.isBooked = isBooked;
        return this;
    }
    @Override
    public SlotBuilder version(Integer version) {
        this.version = version;
        return this;
    }

    @Override
    public Slot build() {
        Slot obj = new Slot();
        obj.setId(this.id);
        obj.setProfessional(this.professional);
        obj.setStartTime(this.startTime);
        obj.setEndTime(this.endTime);
        obj.setBooked(this.isBooked);
        obj.setVersion(this.version);
        return obj;
    }
}
