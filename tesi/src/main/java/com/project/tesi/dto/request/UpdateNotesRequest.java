package com.project.tesi.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateNotesRequest(@Size(max = 1000) String notes) {}
