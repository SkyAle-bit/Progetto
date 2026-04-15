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

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String profilePictureUrl;
}
