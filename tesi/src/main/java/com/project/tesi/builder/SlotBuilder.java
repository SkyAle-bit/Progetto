package com.project.tesi.builder;

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


public interface SlotBuilder {
    SlotBuilder id(Long id);
    SlotBuilder professional(User professional);
    SlotBuilder startTime(LocalDateTime startTime);
    SlotBuilder endTime(LocalDateTime endTime);
    SlotBuilder isBooked(boolean isBooked);
    SlotBuilder version(Integer version);
    Slot build();
}
