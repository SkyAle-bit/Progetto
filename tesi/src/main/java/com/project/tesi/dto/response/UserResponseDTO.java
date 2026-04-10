package com.project.tesi.dto.response;

public record UserResponseDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String role,
        String createdAt,
        String professionalBio,
        String assignedPTName,
        String assignedNutritionistName
) {}
