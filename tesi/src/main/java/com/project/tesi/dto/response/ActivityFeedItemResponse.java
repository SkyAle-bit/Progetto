package com.project.tesi.dto.response;

public record ActivityFeedItemResponse(
        String type,
        String icon,
        String text,
        String timestamp,
        String timeAgo
) {}
