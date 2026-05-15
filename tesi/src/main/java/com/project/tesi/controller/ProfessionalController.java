package com.project.tesi.controller;

import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import com.project.tesi.dto.response.SlotDTO;
import com.project.tesi.enums.Role;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint REST per le funzioni esclusive del professionista (es. gestione slot e recupero clienti assegnati).
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

    /** Crea nuovi slot nel calendario del professionista autenticato. */
    @PostMapping("/slots")
    public ResponseEntity<List<SlotDTO>> createSlots(@AuthenticationPrincipal User user,
                                                      @RequestBody List<SlotDTO> slots) {
        return ResponseEntity.ok(userFacade.createSlots(user.getId(), slots));
    }

    /** Elimina uno slot specifico dal calendario del professionista autenticato. */
    @DeleteMapping("/slots/{slotId}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long slotId) {
        userFacade.deleteSlot(slotId);
        return ResponseEntity.noContent().build();
    }
}
