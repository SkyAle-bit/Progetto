package com.project.tesi.facade;

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

/**
 * Test unitari per {@link AdminFacade}.
 */
@ExtendWith(MockitoExtension.class)
class AdminFacadeTest {

    @Mock private AdminService adminService;
    @Mock private AdminStatsService adminStatsService;

    @InjectMocks
    private AdminFacade adminFacade;

    @Test
    @DisplayName("getAllUsers — delega al AdminService")
    void getAllUsers() {
        List<Map<String, Object>> users = List.of(Map.of("id", 1L));
        when(adminService.getAllUsers()).thenReturn(users);

        assertThat(adminFacade.getAllUsers()).isEqualTo(users);
        verify(adminService).getAllUsers();
    }

    @Test
    @DisplayName("createUser — delega al AdminService")
    void createUser() {
        Map<String, Object> body = Map.of("email", "test@test.com");
        Map<String, Object> result = Map.of("id", 1L);
        when(adminService.createUser(body)).thenReturn(result);

        assertThat(adminFacade.createUser(body)).isEqualTo(result);
    }

    @Test
    @DisplayName("deleteUser — delega al AdminService")
    void deleteUser() {
        adminFacade.deleteUser(1L);
        verify(adminService).deleteUser(1L);
    }

    @Test
    @DisplayName("getAllSubscriptions — delega al AdminService")
    void getAllSubscriptions() {
        List<Map<String, Object>> subs = List.of(Map.of("id", 1L));
        when(adminService.getAllSubscriptions()).thenReturn(subs);

        assertThat(adminFacade.getAllSubscriptions()).isEqualTo(subs);
    }

    @Test
    @DisplayName("createPlan — delega al AdminService")
    void createPlan() {
        Map<String, Object> body = Map.of("name", "Premium");
        Map<String, Object> result = Map.of("id", 1L);
        when(adminService.createPlan(body)).thenReturn(result);

        assertThat(adminFacade.createPlan(body)).isEqualTo(result);
    }

    @Test
    @DisplayName("deletePlan — delega al AdminService")
    void deletePlan() {
        adminFacade.deletePlan(1L);
        verify(adminService).deletePlan(1L);
    }

    @Test
    @DisplayName("getAdminStats — delega al AdminStatsService")
    void getAdminStats() {
        Map<String, Object> stats = Map.of("totalUsers", 50);
        when(adminStatsService.getAdminStats()).thenReturn(stats);

        assertThat(adminFacade.getAdminStats()).isEqualTo(stats);
    }
}

