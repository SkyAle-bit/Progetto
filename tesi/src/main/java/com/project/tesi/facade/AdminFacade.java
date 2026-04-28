package com.project.tesi.facade;

import com.project.tesi.dto.request.PlanCreateRequestDTO;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.service.AdminService;
import com.project.tesi.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Facade per il pannello amministrativo (Design Pattern Facade).
 *
 * Fornisce un punto d'accesso unificato a tutte le operazioni admin.
 */
@Component
@RequiredArgsConstructor
public class AdminFacade {

    private final AdminService adminService;
    private final AdminStatsService adminStatsService;


    public List<UserResponseDTO> getAllUsers() {
        return adminService.getAllUsers().stream()
                .map(FacadeMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDTO createUser(UserCreateRequestDTO request) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", request.email());
        body.put("firstName", request.firstName());
        body.put("lastName", request.lastName());
        body.put("password", request.password());
        body.put("role", request.role());
        if (request.assignedPTId() != null) body.put("assignedPTId", request.assignedPTId());
        if (request.assignedNutritionistId() != null) body.put("assignedNutritionistId", request.assignedNutritionistId());

        return FacadeMapper.mapToUserResponse(adminService.createUser(body));
    }

    public void deleteUser(Long id) {
        adminService.deleteUser(id);
    }


    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        return adminService.getAllSubscriptions().stream()
                .map(FacadeMapper::mapToSubscriptionResponse)
                .collect(Collectors.toList());
    }

    public SubscriptionResponseDTO updateSubscriptionCredits(Long id, int pt, int nutri) {
        return FacadeMapper.mapToSubscriptionResponse(adminService.updateSubscriptionCredits(id, pt, nutri));
    }


    public PlanResponseDTO createPlan(PlanCreateRequestDTO request) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", request.name());
        body.put("duration", request.duration());
        body.put("fullPrice", request.fullPrice());
        body.put("monthlyInstallmentPrice", request.monthlyInstallmentPrice());
        body.put("monthlyCreditsPT", request.monthlyCreditsPT());
        body.put("monthlyCreditsNutri", request.monthlyCreditsNutri());

        return FacadeMapper.mapToPlanResponse(adminService.createPlan(body));
    }

    public void deletePlan(Long id) {
        adminService.deletePlan(id);
    }


    public Map<String, Object> getAdminStats() {
        return adminStatsService.getAdminStats();
    }

}
