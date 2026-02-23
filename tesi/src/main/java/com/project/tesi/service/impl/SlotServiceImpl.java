package com.project.tesi.service.impl;

import com.project.tesi.dto.response.SlotDTO;
import com.project.tesi.enums.Role;
import com.project.tesi.model.Slot;
import com.project.tesi.model.User;
import com.project.tesi.model.WeeklySchedule;
import com.project.tesi.repository.SlotRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.repository.WeeklyScheduleRepository;
import com.project.tesi.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {

    private final SlotRepository slotRepository;
    private final UserRepository userRepository;
    private final WeeklyScheduleRepository weeklyScheduleRepository; // Necessario per le regole orarie

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
                    .isBooked(false)
                    .build();
            slotsToSave.add(slot);
        }

        List<Slot> savedSlots = slotRepository.saveAll(slotsToSave);

        return savedSlots.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Genera automaticamente slot di 30 minuti basandosi sul WeeklySchedule.
     * Cicla i giorni tra startDate e endDate e applica le regole del giorno
     * corrispondente.
     *
     */
    @Override
    @Transactional
    public void generateSlotsFromSchedule(Long professionalId, LocalDate startDate, LocalDate endDate) {
        User professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        // Recupera le fasce orarie predefinite (es. Lunedì 09:00-13:00)
        List<WeeklySchedule> schedules = weeklyScheduleRepository.findByProfessional(professional);

        List<Slot> newSlots = new ArrayList<>();

        // Cicla ogni giorno nel range richiesto
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDay = date;

            // Filtra le regole orarie per il giorno della settimana corrente (es. MONDAY)
            List<WeeklySchedule> dailyRules = schedules.stream()
                    .filter(s -> s.getDayOfWeek().equals(currentDay.getDayOfWeek()))
                    .toList();

            for (WeeklySchedule rule : dailyRules) {
                LocalTime currentTime = rule.getStartTime();

                // Crea slot di 30 min finché c'è spazio nella fascia oraria
                while (currentTime.plusMinutes(30).isBefore(rule.getEndTime()) ||
                        currentTime.plusMinutes(30).equals(rule.getEndTime())) {

                    LocalDateTime startSlot = LocalDateTime.of(currentDay, currentTime);
                    LocalDateTime endSlot = startSlot.plusMinutes(30);

                    // Evita la creazione di slot duplicati
                    if (!slotRepository.existsByProfessionalAndStartTime(professional, startSlot)) {
                        newSlots.add(Slot.builder()
                                .professional(professional)
                                .startTime(startSlot)
                                .endTime(endSlot)
                                .isBooked(false)
                                .build());
                    }

                    currentTime = currentTime.plusMinutes(30);
                }
            }
        }

        if (!newSlots.isEmpty()) {
            slotRepository.saveAll(newSlots);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SlotDTO> getAvailableSlots(Long professionalId) {
        User professional = userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Professionista non trovato"));

        return slotRepository.findByProfessional(professional).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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

    @Scheduled(cron = "0 0 0 * * SUN") // ogni domenica a mezzanotte
    @Transactional
    public void generateWeeklySlotsForAllProfessionals() {
        List<User> professionals = userRepository.findByRoleIn(List.of(Role.PERSONAL_TRAINER, Role.NUTRITIONIST));
        LocalDate start = LocalDate.now().plusDays(7); // tra una settimana
        LocalDate end = start.plusDays(6); // tutta la settimana successiva
        for (User pro : professionals) {
            generateSlotsFromSchedule(pro.getId(), start, end);
        }
    }
}