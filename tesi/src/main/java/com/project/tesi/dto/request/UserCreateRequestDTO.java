package com.project.tesi.dto.request;

public record UserCreateRequestDTO(
        String email,
        String firstName,
        String lastName,
        String password,
        String role,
        Long assignedPTId,
        Long assignedNutritionistId
) {}
