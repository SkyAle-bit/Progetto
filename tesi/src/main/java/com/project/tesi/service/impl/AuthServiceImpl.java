package com.project.tesi.service.impl;

import com.project.tesi.dto.request.LoginRequest;
import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.AuthResponse;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.model.User;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.security.CustomUserDetailsService;
import com.project.tesi.security.JwtUtil;
import com.project.tesi.service.AuthService;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Implementazione del servizio di autenticazione.
 *
 * Gestisce:
 * <ul>
 *   <li>Registrazione: delega a {@link UserService} la creazione dell'utente</li>
 *   <li>Login: autentica via Spring Security, genera il JWT e restituisce il profilo completo</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public UserResponse register(RegisterRequest request) {
        return userService.registerUser(request);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // 1. Spring Security verifica email e password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Carica i dati base per Spring Security
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        // 3. Genera il Token JWT
        String jwtToken = jwtUtil.generateToken(userDetails);

        // 4. Recupera tutti i dati dell'utente dal DB
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utente", "email", request.getEmail()));

        // 5. Costruisce la risposta
        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}

