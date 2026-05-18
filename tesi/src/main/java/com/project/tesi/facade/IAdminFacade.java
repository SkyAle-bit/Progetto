package com.project.tesi.facade;

import com.project.tesi.dto.request.ModeratorUserUpdateRequest;
import com.project.tesi.dto.request.PlanCreateRequestDTO;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.dto.response.stats.AdminStatsResponse;

import java.util.List;

public interface IAdminFacade {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO createUser(UserCreateRequestDTO request);
    void deleteUser(Long id);
    List<SubscriptionResponseDTO> getAllSubscriptions();
    SubscriptionResponseDTO updateSubscriptionCredits(Long id, int pt, int nutri);
    UserResponseDTO updateUser(Long id, ModeratorUserUpdateRequest request);
    PlanResponseDTO createPlan(PlanCreateRequestDTO request);
    PlanResponseDTO updatePlan(Long id, PlanCreateRequestDTO request);
    void deletePlan(Long id);
    AdminStatsResponse getAdminStats();
}
