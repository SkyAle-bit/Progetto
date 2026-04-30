package com.project.tesi.facade;


import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import java.util.List;
import java.util.Map;
import com.project.tesi.dto.request.PlanCreateRequestDTO;

public interface IAdminFacade {
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO createUser(UserCreateRequestDTO request);
    void deleteUser(Long id);
    List<SubscriptionResponseDTO> getAllSubscriptions();
    SubscriptionResponseDTO updateSubscriptionCredits(Long id, int pt, int nutri);
    PlanResponseDTO createPlan(PlanCreateRequestDTO request);
    void deletePlan(Long id);
    Map<String, Object> getAdminStats();
}
