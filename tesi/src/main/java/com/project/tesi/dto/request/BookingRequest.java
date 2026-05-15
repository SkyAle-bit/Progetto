package com.project.tesi.dto.request;

import jakarta.validation.constraints.NotNull;

public record BookingRequest(@NotNull Long slotId) {
}
