package com.project.tesi.dto.request;

import com.project.tesi.enums.PaymentFrequency;
import com.project.tesi.enums.PlanDuration;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long planId;
    @NotNull
    private PaymentFrequency paymentFrequency;
}