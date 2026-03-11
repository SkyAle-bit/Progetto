package com.project.tesi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di richiesta per la creazione di una prenotazione.
 * Il client invia l'ID dell'utente che prenota e l'ID dello slot scelto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {

    /** ID del cliente che effettua la prenotazione. */
    @NotNull
    private Long userId;

    /** ID dello slot temporale da prenotare. */
    @NotNull
    private Long slotId;
}