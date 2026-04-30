package com.project.tesi.dto.request;

import com.project.tesi.enums.PaymentFrequency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO di richiesta per la registrazione di un nuovo cliente.
 * Include i dati anagrafici, le credenziali di accesso e le scelte
 * iniziali relative a professionisti e piano di abbonamento.
 */
@Data
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


    @Schema(description = "ID del piano di abbonamento scelto", example = "1")
    private Long selectedPlanId;

    @Schema(description = "Frequenza di pagamento scelta", example = "MONTHLY")
    private PaymentFrequency paymentFrequency;


    public RegisterRequest() {}

    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private Long selectedPtId;
        private Long selectedNutritionistId;
        private String profilePicture;
        private Long selectedPlanId;
        private PaymentFrequency paymentFrequency;

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder selectedPtId(Long selectedPtId) {
            this.selectedPtId = selectedPtId;
            return this;
        }

        public Builder selectedNutritionistId(Long selectedNutritionistId) {
            this.selectedNutritionistId = selectedNutritionistId;
            return this;
        }

        public Builder profilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
            return this;
        }

        public Builder selectedPlanId(Long selectedPlanId) {
            this.selectedPlanId = selectedPlanId;
            return this;
        }

        public Builder paymentFrequency(PaymentFrequency paymentFrequency) {
            this.paymentFrequency = paymentFrequency;
            return this;
        }

        public RegisterRequest build() {
            RegisterRequest obj = new RegisterRequest();
            obj.firstName = this.firstName;
            obj.lastName = this.lastName;
            obj.email = this.email;
            obj.password = this.password;
            obj.selectedPtId = this.selectedPtId;
            obj.selectedNutritionistId = this.selectedNutritionistId;
            obj.profilePicture = this.profilePicture;
            obj.selectedPlanId = this.selectedPlanId;
            obj.paymentFrequency = this.paymentFrequency;
            return obj;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
