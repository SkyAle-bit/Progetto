package com.project.tesi.dto.response;

import com.project.tesi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // Per i clienti: mostriamo chi li segue
    private String assignedPtName;
    private String assignedNutritionistName;

    // Per i professionisti: statistiche base
    private Double averageRating;
    private Integer activeClientsCount;
}