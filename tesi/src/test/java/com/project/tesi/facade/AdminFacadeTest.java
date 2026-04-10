package com.project.tesi.facade;

import com.project.tesi.dto.request.PlanCreateRequestDTO;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.dto.response.PlanResponseDTO;
import com.project.tesi.dto.response.SubscriptionResponseDTO;
import com.project.tesi.dto.response.UserResponseDTO;
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
        List<Map<String, Object>> users = List.of(Map.of("id", 1L, "firstName", "Test"));
        when(adminService.getAllUsers()).thenReturn(users);

        List<UserResponseDTO> response = adminFacade.getAllUsers();
        assertThat(response.get(0).id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("createUser")
    void createUser() {
        UserCreateRequestDTO request = new UserCreateRequestDTO("test@test.com", "Test", "User", "pass", "CLIENT", null, null);
        Map<String, Object> result = Map.of("id", 1L, "email", "test@test.com");
        when(adminService.createUser(anyMap())).thenReturn(result);

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
        List<Map<String, Object>> subs = List.of(Map.of("id", 1L));
        when(adminService.getAllSubscriptions()).thenReturn(subs);

        List<SubscriptionResponseDTO> response = adminFacade.getAllSubscriptions();
        assertThat(response.get(0).id()).isEqualTo(1L);
    }

    @Test
    @DisplayName("createPlan")
    void createPlan() {
        PlanCreateRequestDTO request = new PlanCreateRequestDTO("Premium", "MENSILE", 100.0, 100.0, 5, 5);
        Map<String, Object> result = Map.of("id", 1L);
        when(adminService.createPlan(anyMap())).thenReturn(result);

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
