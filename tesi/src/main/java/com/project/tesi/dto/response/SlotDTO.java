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

    /** ID dello slot (null se si sta creando un nuovo slot). */
    private Long id;

    /** Data e ora di inizio della fascia oraria. */
    private LocalDateTime startTime;

    /** Data e ora di fine della fascia oraria. */
    private LocalDateTime endTime;

    /** Indica se lo slot è ancora disponibile per la prenotazione. */
    private boolean isAvailable;

    /** ID del professionista proprietario dello slot. */
    private Long professionalId;
}