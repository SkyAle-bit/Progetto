package com.project.tesi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO tipizzato per l'aggiornamento di un utente da parte del moderatore.
 *
 * <p>Sostituisce il precedente parametro {@code Map<String, Object>} che non
 * offriva garanzie a compile-time sui campi accettati né validazione automatica
 * dei dati in ingresso. Tutti i campi sono opzionali: vengono aggiornati solo
 * quelli valorizzati (diversi da {@code null} o stringa vuota).</p>
 *
 * <p>Migliora la <b>type safety</b> del layer Controller→Facade→Service,
 * impedendo l'iniezione di chiavi arbitrarie nella mappa che potrebbero
 * aggirare la validazione applicativa.</p>
 */
public record ModeratorUserUpdateRequest(

        /**
         * Nuovo indirizzo email dell'utente.
         * Se valorizzato, deve rispettare il formato email standard.
         */
        @Email(message = "Il formato dell'email non è valido")
        String email,

        /**
         * Nuovo nome dell'utente.
         * Se valorizzato, deve avere almeno 1 carattere.
         */
        @Size(min = 1, max = 100, message = "Il nome deve essere tra 1 e 100 caratteri")
        String firstName,

        /**
         * Nuovo cognome dell'utente.
         * Se valorizzato, deve avere almeno 1 carattere.
         */
        @Size(min = 1, max = 100, message = "Il cognome deve essere tra 1 e 100 caratteri")
        String lastName,

        /**
         * Nuova password dell'utente in chiaro (sarà codificata prima della persistenza).
         * Se valorizzata, deve avere almeno 6 caratteri.
         */
        @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
        String password,

        /**
         * Nuovo ruolo dell'utente (es. "CLIENT", "PERSONAL_TRAINER", "NUTRITIONIST").
         * Il moderatore può assegnare solo i ruoli del proprio perimetro di competenza.
         */
        String role
) {}
