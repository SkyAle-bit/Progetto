package com.project.tesi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotNull
    private Long userId; // Chi prenota

    @NotNull
    private Long slotId; // Quale slot
}