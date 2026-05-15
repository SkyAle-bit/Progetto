package com.project.tesi.dto.response.stats;

import java.util.List;

public record ProfessionalStatsResponse(
        List<TodayBookingItem> todayBookings,
        int todayBookingsCount,
        List<ClientAttentionItem> clientsNeedingAttention,
        int clientsNeedingAttentionCount,
        int docsUploadedThisWeek,
        int totalClients
) {
    public record TodayBookingItem(
            Long id,
            String clientName,
            Long clientId,
            String startTime,
            String endTime,
            String status,
            String meetingLink
    ) {}

    public record ClientAttentionItem(
            Long id,
            String firstName,
            String lastName,
            String lastDocDate,
            long daysSinceLastDoc
    ) {}
}
