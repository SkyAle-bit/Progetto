package com.project.tesi.dto.request.ws;

import jakarta.validation.constraints.NotBlank;

public record JoinRoomRequest(@NotBlank String roomId) {}
