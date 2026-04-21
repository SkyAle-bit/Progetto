package com.project.tesi.service.impl;

import com.project.tesi.dto.request.LoginRequest;
import com.project.tesi.dto.request.RegisterRequest;
import com.project.tesi.dto.response.AuthResponse;
import com.project.tesi.dto.response.UserResponse;
import com.project.tesi.exception.common.ResourceNotFoundException;
import com.project.tesi.model.PasswordResetToken;
import com.project.tesi.model.User;
import com.project.tesi.repository.PasswordResetTokenRepository;
import com.project.tesi.repository.UserRepository;
import com.project.tesi.security.CustomUserDetailsService;
import com.project.tesi.security.JwtUtil;
import com.project.tesi.service.AuthService;
import com.project.tesi.service.EmailService;
import com.project.tesi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementazione del servizio di autenticazione.
 *
 * Gestisce:
 * <ul>
 *   <li>Registrazione: delega a {@link UserService} la creazione dell'utente</li>
 *   <li>Login: autentica via Spring Security, genera il JWT e restituisce il profilo completo</li>
 *   <li>Recupero password: genera token, invia email e reimposta la password</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse register(RegisterRequest request) {
        return userService.registerUser(request);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        String jwtToken = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utente", "email", request.getEmail()));

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

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente", "email", email));

        // Elimina eventuali token precedenti per questo utente
        passwordResetTokenRepository.deleteByUser(user);

        // Genera un nuovo token con scadenza di 30 minuti
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(30))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        // Invia l'email con il link di reset
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), token);
        } catch (Exception e) {
            log.error("Errore nell'invio dell'email di reset password a {}: {}", email, e.getMessage());
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token di reset non valido."));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Questo link di reset è già stato utilizzato.");
        }

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Il link di reset è scaduto. Richiedi un nuovo reset.");
        }

        // Aggiorna la password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marca il token come usato
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reimpostata con successo per l'utente {}", user.getEmail());
    }
}
