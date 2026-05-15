package com.project.tesi.dto.request;

import com.project.tesi.enums.PaymentFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dati necessari per la registrazione di un nuovo cliente")
public record RegisterRequest(
        @Schema(description = "Il nome del cliente", example = "Ciccio")
        @NotBlank(message = "Il nome è obbligatorio")
        String firstName,

        @Schema(description = "Il cognome del cliente", example = "Rimi")
        @NotBlank(message = "Il cognome è obbligatorio")
        String lastName,

        @Schema(description = "L'indirizzo email (usato per il login)", example = "ciccio.rimi@example.com")
        @Email(message = "Email non valida")
        @NotBlank(message = "L'email è obbligatoria")
        String email,

        @Schema(description = "La password dell'account (minimo 6 caratteri)", example = "Password123!")
        @NotBlank
        @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
        String password,

        @Schema(description = "ID del Personal Trainer scelto durante la registrazione", example = "1")
        Long selectedPtId,

        @Schema(description = "ID del Nutrizionista scelto durante la registrazione", example = "2")
        Long selectedNutritionistId,

        String profilePicture,

        @Schema(description = "ID del piano di abbonamento scelto", example = "1")
        Long selectedPlanId,

        @Schema(description = "Frequenza di pagamento scelta", example = "MONTHLY")
        PaymentFrequency paymentFrequency) {
}
