package com.project.tesi.facade;

import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;

import java.util.Map;

/**
 * Mapper condiviso per convertire le Map (dal service) in DTO tipizzati
 * da restituire tramite i controller amministrativi/moderatori.
 */
public class FacadeMapper {

    public static UserResponseDTO mapToUserResponse(Map<String, Object> map) {
        if (map == null) return null;
        return new UserResponseDTO(
                map.get("id") != null ? Long.parseLong(map.get("id").toString()) : null,
                (String) map.get("firstName"),
                (String) map.get("lastName"),
                (String) map.get("email"),
                (String) map.get("role"),
                (String) map.get("createdAt"),
                (String) map.get("professionalBio"),
                (String) map.get("assignedPTName"),
                (String) map.get("assignedNutritionistName")
        );
    }

    public static SubscriptionResponseDTO mapToSubscriptionResponse(Map<String, Object> map) {
        if (map == null) return null;
        return new SubscriptionResponseDTO(
                map.get("id") != null ? Long.parseLong(map.get("id").toString()) : null,
                map.get("userId") != null ? Long.parseLong(map.get("userId").toString()) : null,
                (String) map.get("userName"),
                (String) map.get("planName"),
                map.get("active") != null ? (Boolean) map.get("active") : false,
                (String) map.get("startDate"),
                (String) map.get("endDate"),
                map.get("monthlyPrice") != null ? Double.parseDouble(map.get("monthlyPrice").toString()) : 0.0,
                map.get("currentCreditsPT") != null ? Integer.parseInt(map.get("currentCreditsPT").toString()) : 0,
                map.get("currentCreditsNutri") != null ? Integer.parseInt(map.get("currentCreditsNutri").toString()) : 0
        );
    }

    public static PlanResponseDTO mapToPlanResponse(Map<String, Object> map) {
        if (map == null) return null;
        return new PlanResponseDTO(
                map.get("id") != null ? Long.parseLong(map.get("id").toString()) : null,
                (String) map.get("name"),
                (String) map.get("duration"),
                map.get("fullPrice") != null ? Double.parseDouble(map.get("fullPrice").toString()) : null,
                map.get("monthlyInstallmentPrice") != null ? Double.parseDouble(map.get("monthlyInstallmentPrice").toString()) : null,
                map.get("monthlyCreditsPT") != null ? Integer.parseInt(map.get("monthlyCreditsPT").toString()) : 0,
                map.get("monthlyCreditsNutri") != null ? Integer.parseInt(map.get("monthlyCreditsNutri").toString()) : 0
        );
    }
}
