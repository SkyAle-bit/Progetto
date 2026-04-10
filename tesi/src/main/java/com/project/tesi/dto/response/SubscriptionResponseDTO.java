package com.project.tesi.dto.response;

public record SubscriptionResponseDTO(
        Long id,
        Long userId,
        String userName,
        String planName,
        Boolean active,
        String startDate,
        String endDate,
        Double monthlyPrice,
        Integer currentCreditsPT,
        Integer currentCreditsNutri
) {}
