package com.project.tesi.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateCreditsRequest(
        @Min(0) int creditsPT,
        @Min(0) int creditsNutri
) {}
