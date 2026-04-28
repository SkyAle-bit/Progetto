package com.project.tesi.facade;

import com.project.tesi.dto.request.PlanCreateRequestDTO;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.service.AdminService;
import com.project.tesi.service.AdminStatsService;
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
public class AdminFacade {

    private final AdminService adminService;
    private final AdminStatsService adminStatsService;

    // Costruttore esplicito — pattern Facade
    public AdminFacade(AdminService adminService, AdminStatsService adminStatsService) {
        this.adminService = adminService;
        this.adminStatsService = adminStatsService;
    }


    public List<UserResponseDTO> getAllUsers() {
        return adminService.getAllUsers().stream()
                .map(FacadeMapper::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDTO createUser(UserCreateRequestDTO request) {
        return FacadeMapper.mapToUserResponse(adminService.createUser(request));
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
        return FacadeMapper.mapToPlanResponse(adminService.createPlan(request));
    }

    public void deletePlan(Long id) {
        adminService.deletePlan(id);
    }


    public Map<String, Object> getAdminStats() {
        return adminStatsService.getAdminStats();
    }

}
