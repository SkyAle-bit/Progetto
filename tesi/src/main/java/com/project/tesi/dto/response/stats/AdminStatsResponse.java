package com.project.tesi.dto.response.stats;

import java.util.List;
import java.util.Map;

public record AdminStatsResponse(
        Map<String, Long> usersByRole,
        int totalUsers,
        List<MonthlyUserCount> usersPerMonth,
        List<PlanPopularityItem> planPopularity,
        long totalActiveSubscriptions,
        long totalSubscriptions,
        CreditsStats credits,
        double monthlyRevenue,
        double yearlyRevenue,
        long bookingsThisMonth,
        long bookingsTotal,
        List<ProfessionalWorkloadItem> professionalWorkload
) {
    public record MonthlyUserCount(String month, int year, long count) {}

    public record PlanPopularityItem(
            String name,
            long activeCount,
            long percentage,
            double monthlyPrice,
            double fullPrice
    ) {}

    public record CreditsStats(
            int ptAvailable,
            int ptTotal,
            int ptConsumed,
            long ptPercentUsed,
            int nutriAvailable,
            int nutriTotal,
            int nutriConsumed,
            long nutriPercentUsed
    ) {}

    public record ProfessionalWorkloadItem(String name, String role, long clientCount) {}
}
