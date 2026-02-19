package com.project.tesi.controller;

import com.project.tesi.dto.request.LoginRequest;
import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        // TODO: Qui implementeremo il token JWT. Per ora restituisce una stringa.
        return ResponseEntity.ok("Login simulato con successo per: " + request.getEmail());
    }

    // Endpoint comodissimo per testare se il server è online!
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Il Backend è online e funziona correttamente! 🚀");
    }
}