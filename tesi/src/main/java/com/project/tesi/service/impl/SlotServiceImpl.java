package com.project.tesi.service.impl;

import com.project.tesi.dto.response.SlotDTO;
import com.project.tesi.model.Slot;
import com.project.tesi.model.User;
import com.project.tesi.repository.SlotRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<SlotDTO> createSlots(Long professionalId, List<SlotDTO> slotsDTO) {
        User professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        List<Slot> slotsToSave = new ArrayList<>();

        for (SlotDTO dto : slotsDTO) {
            Slot slot = Slot.builder()
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .professional(professional)
                    .isBooked(false) // Appena creato è libero
                    .build();
            slotsToSave.add(slot);
        }

        List<Slot> savedSlots = slotRepository.saveAll(slotsToSave);

        return savedSlots.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotDTO> getAvailableSlots(Long professionalId) {
        User professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        return slotRepository.findByProfessionalAndIsBookedFalse(professional).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSlot(Long slotId) {
        slotRepository.deleteById(slotId);
    }

    private SlotDTO mapToDTO(Slot slot) {
        return SlotDTO.builder()
                .id(slot.getId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .isAvailable(!slot.isBooked())
                .professionalId(slot.getProfessional().getId())
                .build();
    }
}