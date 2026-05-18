package com.project.tesi.facade.impl;

import com.project.tesi.dto.request.ModeratorUserUpdateRequest;
import com.project.tesi.dto.request.PlanCreateRequestDTO;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.dto.response.stats.AdminStatsResponse;
import com.project.tesi.facade.FacadeMapper;
import com.project.tesi.facade.IAdminFacade;
import com.project.tesi.service.AdminService;
import com.project.tesi.service.AdminStatsService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementazione del facade per il pannello di amministrazione.
 */
@Component
public class AdminFacadeImpl implements IAdminFacade {

    private final AdminService adminService;
    private final AdminStatsService adminStatsService;
    private final FacadeMapper facadeMapper;

    public AdminFacadeImpl(AdminService adminService, AdminStatsService adminStatsService, FacadeMapper facadeMapper) {
        this.adminService = adminService;
        this.adminStatsService = adminStatsService;
        this.facadeMapper = facadeMapper;
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return adminService.getAllUsers().stream()
                .map(facadeMapper::mapToUserResponse)
                .toList();
    }

    @Override
    public UserResponseDTO createUser(UserCreateRequestDTO request) {
        return facadeMapper.mapToUserResponse(adminService.createUser(request));
    }

    @Override
    public void deleteUser(Long id) {
        adminService.deleteUser(id);
    }

    @Override
    public List<SubscriptionResponseDTO> getAllSubscriptions() {
        return adminService.getAllSubscriptions().stream()
                .map(facadeMapper::mapToSubscriptionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionResponseDTO updateSubscriptionCredits(Long id, int pt, int nutri) {
        return facadeMapper.mapToSubscriptionResponse(adminService.updateSubscriptionCredits(id, pt, nutri));
    }

    @Override
    public UserResponseDTO updateUser(Long id, ModeratorUserUpdateRequest request) {
        return facadeMapper.mapToUserResponse(adminService.updateUser(id, request));
    }

    @Override
    public PlanResponseDTO createPlan(PlanCreateRequestDTO request) {
        return facadeMapper.mapToPlanResponse(adminService.createPlan(request));
    }

    @Override
    public PlanResponseDTO updatePlan(Long id, PlanCreateRequestDTO request) {
        return facadeMapper.mapToPlanResponse(adminService.updatePlan(id, request));
    }

    @Override
    public void deletePlan(Long id) {
        adminService.deletePlan(id);
    }

    @Override
    public AdminStatsResponse getAdminStats() {
        return adminStatsService.getAdminStats();
    }
}
