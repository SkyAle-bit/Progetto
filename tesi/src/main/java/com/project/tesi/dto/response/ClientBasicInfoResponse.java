package com.project.tesi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta con le informazioni base di un cliente.
 * Usato dal professionista per visualizzare la lista dei propri clienti
 * e dal sistema per restituire l'account Admin (per avviare chat).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientBasicInfoResponse {

    /** ID dell'utente. */
    private Long id;

    /** Nome dell'utente. */
    private String firstName;

    /** Cognome dell'utente. */
    private String lastName;

    /** Email dell'utente. */
    private String email;

    /** URL dell'immagine profilo. */
    private String profilePictureUrl;
}
