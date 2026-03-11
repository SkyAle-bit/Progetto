package com.project.tesi.service;

import com.project.tesi.dto.request.LoginRequest;
import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.AuthResponse;
import com.project.tesi.dto.response.UserResponse;

/**
 * Interfaccia del servizio di autenticazione.
 * Gestisce la registrazione di nuovi clienti e il login con generazione JWT.
 */
public interface AuthService {

    /** Registra un nuovo cliente e restituisce il profilo creato. */
    UserResponse register(RegisterRequest request);

    /** Autentica un utente e restituisce il token JWT con i dati del profilo. */
    AuthResponse login(LoginRequest request);
}
