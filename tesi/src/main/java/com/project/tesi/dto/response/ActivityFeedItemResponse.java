package com.project.tesi.dto.response;

import java.time.LocalDateTime;

public record ActivityFeedItemResponse(
        String type,
        String text,
        LocalDateTime timestamp
) {}
