package com.project.tesi.builder;

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
