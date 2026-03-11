package com.project.tesi.controller;

import com.project.tesi.dto.request.LoginRequest;
import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.AuthResponse;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.enums.Role;
import com.project.tesi.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test unitari per {@link AuthController}.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("register — restituisce 200 con il profilo creato")
    void register() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("mario@test.com");
        UserResponse userResp = UserResponse.builder().id(1L).email("mario@test.com").role(Role.CLIENT).build();
        when(authService.register(req)).thenReturn(userResp);

        ResponseEntity<UserResponse> response = authController.register(req);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getEmail()).isEqualTo("mario@test.com");
    }

    @Test
    @DisplayName("login — restituisce 200 con token JWT")
    void login() {
        LoginRequest req = new LoginRequest();
        req.setEmail("mario@test.com");
        req.setPassword("password");
        AuthResponse authResp = AuthResponse.builder().token("jwt-123").id(1L).build();
        when(authService.login(req)).thenReturn(authResp);

        ResponseEntity<AuthResponse> response = authController.login(req);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getToken()).isEqualTo("jwt-123");
    }

    @Test
    @DisplayName("ping — restituisce messaggio di health check")
    void ping() {
        ResponseEntity<String> response = authController.ping();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains("online");
    }
}

