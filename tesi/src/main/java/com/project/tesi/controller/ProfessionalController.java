package com.project.tesi.controller;

import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import com.project.tesi.dto.response.SlotDTO;
import com.project.tesi.enums.Role;
import com.project.tesi.facade.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST per la gestione dei professionisti e dei loro slot.
 * Fornisce la vetrina pubblica per i clienti e le operazioni
 * di gestione calendario per i professionisti stessi.
 * Delega alla {@link UserFacade} (pattern Facade).
 */
@RestController
@RequestMapping("/api/professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final UserFacade userFacade;

    /** Restituisce la lista dei professionisti di un certo ruolo (PT o Nutrizionista). */
    @GetMapping
    public ResponseEntity<List<ProfessionalSummaryDTO>> getProfessionals(@RequestParam Role role) {
        return ResponseEntity.ok(userFacade.findAvailableProfessionals(role));
    }

    /** Restituisce gli slot liberi di un professionista (per il calendario prenotazioni). */
    @GetMapping("/{id}/slots")
    public ResponseEntity<List<SlotDTO>> getProfessionalSlots(@PathVariable Long id) {
        return ResponseEntity.ok(userFacade.getAvailableSlots(id));
    }

    /** Crea nuovi slot nel calendario di un professionista. */
    @PostMapping("/{id}/slots")
    public ResponseEntity<List<SlotDTO>> createSlots(@PathVariable Long id, @RequestBody List<SlotDTO> slots) {
        return ResponseEntity.ok(userFacade.createSlots(id, slots));
    }

    /** Elimina uno slot specifico dal calendario di un professionista. */
    @DeleteMapping("/{id}/slots/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id, @PathVariable Long slotId) {
        userFacade.deleteSlot(slotId);
        return ResponseEntity.noContent().build();
    }
}