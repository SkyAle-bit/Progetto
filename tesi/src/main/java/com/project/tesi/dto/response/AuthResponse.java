package com.project.tesi.dto.response;

import com.project.tesi.enums.Role;
import lombok.Data;

/**
 * DTO di risposta restituito dopo un login riuscito.
 * Contiene il token JWT e i dati principali dell'utente autenticato,
 * necessari al frontend per inizializzare la sessione.
 *
 * <p>Implementa manualmente il Design Pattern <b>Builder</b> tramite la classe
 * statica interna {@link Builder}, evitando la generazione automatica di Lombok.
 * Questo permette una costruzione fluente, leggibile e controllata dell'oggetto.</p>
 */
@Data
public class AuthResponse {

    private String token;
    private String type;
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String profilePicture;

    // Costruttore privato: la creazione dell'oggetto è delegata al Builder
    private AuthResponse() {
        this.type = "Bearer"; // valore di default — equivalente a @Builder.Default
    }

    // ──────────────────────────────────────────────────
    //  Builder interno statico — Design Pattern Builder
    // ──────────────────────────────────────────────────

    /**
     * Builder interno per la costruzione fluente di {@link AuthResponse}.
     *
     * <p>Garantisce che il campo {@code type} sia sempre inizializzato al valore
     * di default {@code "Bearer"}, replicando il comportamento di {@code @Builder.Default}
     * di Lombok senza dipendenze dal framework di generazione del codice.</p>
     */
    public static class Builder {

        private String token;
        private String type = "Bearer"; // default esplicito
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private Role role;
        private String profilePicture;

        public Builder token(String token) {
            this.token = token;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

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

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder profilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
            return this;
        }

        /**
         * Costruisce e restituisce l'istanza di {@link AuthResponse}.
         *
         * @return una nuova istanza con i valori impostati tramite il Builder
         */
        public AuthResponse build() {
            AuthResponse response = new AuthResponse();
            response.token = this.token;
            response.type = this.type;
            response.id = this.id;
            response.firstName = this.firstName;
            response.lastName = this.lastName;
            response.email = this.email;
            response.role = this.role;
            response.profilePicture = this.profilePicture;
            return response;
        }
    }

    /** Punto di ingresso statico per la costruzione fluente. */
    public static Builder builder() {
        return new Builder();
    }
}