package com.project.tesi.dto.response;

public record PlanResponseDTO(
        Long id,
        String name,
        String duration,
        Double fullPrice,
        Double monthlyInstallmentPrice,
        Integer monthlyCreditsPT,
        Integer monthlyCreditsNutri
) {}
