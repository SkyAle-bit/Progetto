package com.project.tesi.service;

import com.project.tesi.dto.response.SlotDTO;
import java.time.LocalDate;
import java.util.List;

public interface SlotService {
    List<SlotDTO> createSlots(Long professionalId, List<SlotDTO> slotsDTO);
    List<SlotDTO> getAvailableSlots(Long professionalId);
    void deleteSlot(Long slotId);

    // Aggiungi questa riga:
    void generateSlotsFromSchedule(Long professionalId, LocalDate startDate, LocalDate endDate);
}