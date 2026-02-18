package com.project.tesi.service;

import com.project.tesi.dto.response.SlotDTO;
import java.util.List;

public interface SlotService {
    // Il professionista crea una lista di slot (es. per tutta la settimana)
    List<SlotDTO> createSlots(Long professionalId, List<SlotDTO> slotsDTO);

    // Recupera slot liberi per un professionista
    List<SlotDTO> getAvailableSlots(Long professionalId);

    void deleteSlot(Long slotId);
}