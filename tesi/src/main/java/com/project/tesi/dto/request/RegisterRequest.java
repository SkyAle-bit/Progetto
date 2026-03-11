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

/**
 * DTO di richiesta per la registrazione di un nuovo cliente.
 * Include i dati anagrafici, le credenziali di accesso e le scelte
 * iniziali relative a professionisti e piano di abbonamento.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dati necessari per la registrazione di un nuovo cliente")
public class RegisterRequest {

    /** Nome del cliente. */
    @Schema(description = "Il nome del cliente", example = "Ciccio")
    @NotBlank(message = "Il nome è obbligatorio")
    private String firstName;

    /** Cognome del cliente. */
    @Schema(description = "Il cognome del cliente", example = "Rimi")
    @NotBlank(message = "Il cognome è obbligatorio")
    private String lastName;

    /** Indirizzo email, usato come username per il login. */
    @Schema(description = "L'indirizzo email (usato per il login)", example = "ciccio.rimi@example.com")
    @Email(message = "Email non valida")
    @NotBlank(message = "L'email è obbligatoria")
    private String email;

    /** Password dell'account (minimo 6 caratteri). */
    @Schema(description = "La password dell'account (minimo 6 caratteri)", example = "Password123!")
    @NotBlank
    @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
    private String password;

    /** ID del Personal Trainer scelto durante la registrazione (opzionale). */
    @Schema(description = "ID del Personal Trainer scelto durante la registrazione", example = "1")
    private Long selectedPtId;

    /** ID del Nutrizionista scelto durante la registrazione (opzionale). */
    @Schema(description = "ID del Nutrizionista scelto durante la registrazione", example = "2")
    private Long selectedNutritionistId;

    /** Immagine profilo in formato Base64 (opzionale). */
    private String profilePicture;

    // ── CAMPI PER L'ABBONAMENTO ─────────────────────────────────

    /** ID del piano di abbonamento scelto durante la registrazione (opzionale). */
    @Schema(description = "ID del piano di abbonamento scelto", example = "1")
    private Long selectedPlanId;

    /** Frequenza di pagamento scelta per l'abbonamento (opzionale). */
    @Schema(description = "Frequenza di pagamento scelta", example = "MONTHLY")
    private PaymentFrequency paymentFrequency;

}