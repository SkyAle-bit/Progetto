package com.project.tesi.controller;

import com.project.tesi.dto.response.ClientDashboardResponse;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Il client vede i suoi dati e i PT/Nutrizionisti che lo seguono
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<ClientDashboardResponse> getDashboard(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getClientDashboard(userId));
    }
}