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
 * Gestisce tutto il flusso di autenticazione e sicurezza degli account.
 * 
 * Separiamo chiaramente le responsabilità:
 * - Registrazione: deleghiamo a UserService per la creazione fisica dell'entità.
 * - Login: ci appoggiamo a Spring Security e generiamo il JWT.
 * - Reset Password: usiamo token temporanei (30 minuti) per garantire che i link di reset 
 *   non rimangano validi all'infinito.
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

        // Elimina eventuali token di reset precedenti per questo utente.
        // Facciamo questo per evitare "spam" di link validi che potrebbero essere intercettati.
        passwordResetTokenRepository.deleteByUser(user);

        // Genera un nuovo token che scade tra soli 30 minuti
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

        // Aggiorniamo la password. Attenzione: usiamo BCrypt, che è lento per design.
        // Questo ritardo intenzionale scoraggia gli attacchi brute-force.
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marca il token come usato
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        try {
            emailService.sendPasswordChangeEmail(user.getEmail(), user.getFirstName());
        } catch (Exception e) {
            log.warn("Impossibile inviare email di avvenuto reset password a {}: {}", user.getEmail(), e.getMessage());
        }

        log.info("Password reimpostata con successo per l'utente {}", user.getEmail());
    }
}
