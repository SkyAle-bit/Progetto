package com.project.tesi.facade;

import com.project.tesi.dto.request.PlanCreateRequestDTO;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.service.AdminService;
import com.project.tesi.service.AdminStatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminFacadeTest {

    @Mock private AdminService adminService;
    @Mock private AdminStatsService adminStatsService;

    @InjectMocks
    private AdminFacade adminFacade;

    @Test
    @DisplayName("getAllUsers")
    void getAllUsers() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Test");
        when(adminService.getAllUsers()).thenReturn(List.of(user));

        List<UserResponseDTO> response = adminFacade.getAllUsers();
        assertThat(response.get(0).id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("createUser")
    void createUser() {
        UserCreateRequestDTO request = new UserCreateRequestDTO("test@test.com", "Test", "User", "pass", "CLIENT", null, null);
        User result = new User();
        result.setId(1L);
        result.setEmail("test@test.com");
        when(adminService.createUser(any())).thenReturn(result);

        UserResponseDTO response = adminFacade.createUser(request);
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("deleteUser")
    void deleteUser() {
        adminFacade.deleteUser(1L);
        verify(adminService).deleteUser(1L);
    }

    @Test
    @DisplayName("getAllSubscriptions")
    void getAllSubscriptions() {
        Subscription sub = new Subscription();
        sub.setId(1L);
        when(adminService.getAllSubscriptions()).thenReturn(List.of(sub));

        List<SubscriptionResponseDTO> response = adminFacade.getAllSubscriptions();
        assertThat(response.get(0).id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("createPlan")
    void createPlan() {
        PlanCreateRequestDTO request = new PlanCreateRequestDTO("Premium", "MENSILE", 100.0, 100.0, 5, 5);
        Plan result = new Plan();
        result.setId(1L);
        when(adminService.createPlan(any())).thenReturn(result);

        PlanResponseDTO response = adminFacade.createPlan(request);
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("deletePlan")
    void deletePlan() {
        adminFacade.deletePlan(1L);
        verify(adminService).deletePlan(1L);
    }

    @Test
    @DisplayName("getAdminStats")
    void getAdminStats() {
        Map<String, Object> stats = Map.of("totalUsers", 50);
        when(adminStatsService.getAdminStats()).thenReturn(stats);

        assertThat(adminFacade.getAdminStats()).isEqualTo(stats);
    }
}
