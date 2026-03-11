package com.project.tesi.service;

import com.project.tesi.dto.response.SlotDTO;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaccia del servizio per la gestione degli slot del calendario dei professionisti.
 * Permette la creazione, il recupero e l'eliminazione degli slot disponibili.
 */
public interface SlotService {

    /** Crea nuovi slot nel calendario di un professionista. */
    List<SlotDTO> createSlots(Long professionalId, List<SlotDTO> slotsDTO);

    /** Restituisce gli slot disponibili (futuri e non prenotati) di un professionista. */
    List<SlotDTO> getAvailableSlots(Long professionalId);

    /** Elimina uno slot dal calendario. */
    void deleteSlot(Long slotId);

    /**
     * Genera automaticamente gli slot di un professionista in base al suo
     * orario settimanale per un intervallo di date.
     *
     * @param professionalId ID del professionista
     * @param startDate      data di inizio generazione
     * @param endDate        data di fine generazione
     */
    void generateSlotsFromSchedule(Long professionalId, LocalDate startDate, LocalDate endDate);
}