package com.project.tesi.dto.response;

import com.project.tesi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta con il profilo completo di un utente.
 * Include dati anagrafici, ruolo e informazioni aggiuntive
 * a seconda del tipo di utente (cliente o professionista).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private Role role;


    private String assignedPtName;

    private String assignedNutritionistName;


    private Double averageRating;

    private Integer activeClientsCount;
}