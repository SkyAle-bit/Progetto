package com.project.tesi.security;

import com.project.tesi.model.User;
import com.project.tesi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementazione di {@link UserDetailsService} per integrare il modello utente
 * dell'applicazione con Spring Security.
 *
 * Carica l'utente dal database tramite email e lo converte in un
 * {@link UserDetails} con l'authority {@code ROLE_<ruolo>} per il sistema di autorizzazione.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carica un utente dal database tramite email.
     * Converte il modello {@link User} nel formato {@link UserDetails} richiesto da Spring Security,
     * assegnando l'authority basata sul ruolo (es. ROLE_CLIENT, ROLE_PERSONAL_TRAINER).
     *
     * @param email l'indirizzo email dell'utente (usato come username)
     * @return l'oggetto UserDetails per Spring Security
     * @throws UsernameNotFoundException se l'email non corrisponde a nessun utente
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Cerca l'utente nel tuo database tramite la mail
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));

        // Converte il tuo "User" nel formato "UserDetails" che piace a Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
}