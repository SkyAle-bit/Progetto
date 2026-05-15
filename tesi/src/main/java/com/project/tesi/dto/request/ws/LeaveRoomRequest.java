package com.project.tesi.dto.request.ws;

import jakarta.validation.constraints.NotBlank;

public record LeaveRoomRequest(@NotBlank String roomId) {}
