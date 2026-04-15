package com.project.tesi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO di risposta per uno slot del calendario di un professionista.
 * Usato sia in lettura (calendario disponibilità) che in scrittura
 * (creazione manuale di nuovi slot da parte del professionista).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotDTO {

    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean isAvailable;

    private Long professionalId;
}