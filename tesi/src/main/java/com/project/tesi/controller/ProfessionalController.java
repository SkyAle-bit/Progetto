package com.project.tesi.controller;

import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import com.project.tesi.dto.response.SlotDTO;
import com.project.tesi.enums.Role;
import com.project.tesi.service.SlotService;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/professionals")
@RequiredArgsConstructor
public class ProfessionalController {

    private final UserService userService;
    private final SlotService slotService;

    // Recupera la lista dei professionisti (PT o Nutrizionisti)
    @GetMapping
    public ResponseEntity<List<ProfessionalSummaryDTO>> getProfessionals(@RequestParam Role role) {
        return ResponseEntity.ok(userService.findAvailableProfessionals(role));
    }

    // Recupera gli slot liberi di un professionista specifico
    @GetMapping("/{id}/slots")
    public ResponseEntity<List<SlotDTO>> getProfessionalSlots(@PathVariable Long id) {
        return ResponseEntity.ok(slotService.getAvailableSlots(id));
    }

    // Il professionista crea nuovi slot per la settimana
    @PostMapping("/{id}/slots")
    public ResponseEntity<List<SlotDTO>> createSlots(@PathVariable Long id, @RequestBody List<SlotDTO> slots) {
        return ResponseEntity.ok(slotService.createSlots(id, slots));
    }
}