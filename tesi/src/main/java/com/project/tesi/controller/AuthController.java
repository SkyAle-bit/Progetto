package com.project.tesi.controller;

import com.project.tesi.dto.request.ForgotPasswordRequest;
import com.project.tesi.dto.request.LoginRequest;
import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.request.ResetPasswordRequest;
import com.project.tesi.dto.response.AuthResponse;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller REST per l'autenticazione degli utenti.
 * Gestisce registrazione, login, recupero password e health-check del backend.
 * Tutta la logica è delegata all'{@link AuthService}.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Registra un nuovo cliente e restituisce i dati del profilo creato. */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /** Autentica un utente e restituisce il token JWT con i dati del profilo. */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /** Richiede il reset della password inviando un link via email. */
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "Link di reset inviato. Controlla la tua casella di posta."));
    }

    /** Reimposta la password usando il token ricevuto via email. */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password reimpostata con successo."));
    }

    /** Endpoint di health-check per verificare che il backend sia raggiungibile. */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        return ResponseEntity.ok(Map.of("status", "UP", "message", "Il Backend è online e funziona correttamente"));
    }
}