package com.project.tesi.dto.request;

import com.project.tesi.enums.PaymentFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dati necessari per la registrazione di un nuovo cliente")
public class RegisterRequest {

    @Schema(description = "Il nome del cliente", example = "Ciccio")
    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    @Schema(description = "Il cognome del cliente", example = "Rimi")
    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    @Schema(description = "L'indirizzo email (usato per il login)", example = "ciccio.rimi@example.com")
    @Email(message = "Email non valida")
    @NotBlank(message = "L'email è obbligatoria")
    private String email;

    @Schema(description = "La password dell'account (minimo 6 caratteri)", example = "Password123!")
    @NotBlank
    @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
    private String password;

    @Schema(description = "ID del Personal Trainer scelto durante la registrazione", example = "1")
    private Long selectedPtId;

    @Schema(description = "ID del Nutrizionista scelto durante la registrazione", example = "2")
    private Long selectedNutritionistId;

    private String profilePicture;

    // --- NUOVI CAMPI PER L'ABBONAMENTO ---
    @Schema(description = "ID del piano di abbonamento scelto", example = "1")
    private Long selectedPlanId;

    @Schema(description = "Frequenza di pagamento scelta", example = "MONTHLY")
    private PaymentFrequency paymentFrequency;

}