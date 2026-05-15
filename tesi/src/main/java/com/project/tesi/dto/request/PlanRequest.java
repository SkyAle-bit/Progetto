package com.project.tesi.dto.request;

import com.project.tesi.enums.PaymentFrequency;
import jakarta.validation.constraints.NotNull;

public record PlanRequest(
        @NotNull Long planId,
        @NotNull PaymentFrequency paymentFrequency) {
}
