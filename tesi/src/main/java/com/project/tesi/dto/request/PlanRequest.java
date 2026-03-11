package com.project.tesi.dto.request;

import com.project.tesi.enums.PaymentFrequency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di richiesta per l'attivazione di un abbonamento.
 * Il client specifica l'utente, il piano scelto e la modalità di pagamento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequest {

    /** ID del cliente che attiva l'abbonamento. */
    @NotNull
    private Long userId;

    /** ID del piano commerciale scelto. */
    @NotNull
    private Long planId;

    /** Modalità di pagamento: UNICA_SOLUZIONE o RATE_MENSILI. */
    @NotNull
    private PaymentFrequency paymentFrequency;
}