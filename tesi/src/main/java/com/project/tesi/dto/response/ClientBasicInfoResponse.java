package com.project.tesi.dto.response;

import lombok.Data;

/**
 * DTO di risposta con le informazioni base di un cliente.
 * Usato dal professionista per visualizzare la lista dei propri clienti
 * e dal sistema per restituire l'account Admin (per avviare chat).
 */
@Data
public class ClientBasicInfoResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String profilePictureUrl;

    private ClientBasicInfoResponse() {}

    public static class Builder {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String profilePictureUrl;

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

        public Builder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public ClientBasicInfoResponse build() {
            ClientBasicInfoResponse obj = new ClientBasicInfoResponse();
            obj.id = this.id;
            obj.firstName = this.firstName;
            obj.lastName = this.lastName;
            obj.email = this.email;
            obj.profilePictureUrl = this.profilePictureUrl;
            return obj;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
