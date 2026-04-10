package com.project.tesi.dto.request;

public record PlanCreateRequestDTO(
        String name,
        String duration,
        Double fullPrice,
        Double monthlyInstallmentPrice,
        Integer monthlyCreditsPT,
        Integer monthlyCreditsNutri
) {}
