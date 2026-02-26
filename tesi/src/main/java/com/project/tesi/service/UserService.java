package com.project.tesi.service;

import com.project.tesi.dto.response.ClientDashboardResponse;
import com.project.tesi.dto.response.ProfessionalSummaryDTO;
import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.enums.Role;

import java.util.List;

public interface UserService {

    UserResponse registerUser(RegisterRequest request);

    List<ProfessionalSummaryDTO> findAvailableProfessionals(Role role);

    ClientDashboardResponse getClientDashboard(Long userId);

    List<com.project.tesi.dto.response.ClientBasicInfoResponse> getClientsForProfessional(Long professionalId);
}