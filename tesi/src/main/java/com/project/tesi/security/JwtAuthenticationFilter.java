package com.project.tesi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * Filtro di autenticazione JWT eseguito una volta per ogni richiesta HTTP.
 *
 * Intercetta l'header {@code Authorization: Bearer <token>}, estrae e valida il JWT,
 * e se valido imposta l'autenticazione nel {@link SecurityContextHolder}
 * in modo che i controller possano accedere all'utente autenticato.
 *
 * Se il token è assente, scaduto o non valido, la richiesta prosegue
 * senza autenticazione e verrà bloccata dalle regole di {@link SecurityConfig}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Filtra ogni richiesta HTTP per verificare la presenza di un token JWT valido.
     *
     * Flusso:
     * <ol>
     *   <li>Estrae l'header "Authorization" dalla richiesta</li>
     *   <li>Se assente o non inizia con "Bearer ", lascia passare la richiesta</li>
     *   <li>Estrae il token (rimuovendo il prefisso "Bearer ")</li>
     *   <li>Estrae l'email dal token</li>
     *   <li>Se l'utente non è già autenticato, carica i dati dal DB</li>
     *   <li>Valida il token (firma + scadenza)</li>
     *   <li>Se valido, salva l'autenticazione nel contesto di Spring Security</li>
     *   <li>Passa alla prossima fase della filter chain</li>
     * </ol>
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Estrae l'header "Authorization" dalla richiesta
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Se non c'è il token, o non inizia con "Bearer ", lascia passare la richiesta
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Estrae il Token (tolto il prefisso "Bearer ")
        jwt = authHeader.substring(7);

        try {
            // 4. Estrae l'email dal token
            userEmail = jwtUtil.extractUsername(jwt);

            // 5. Se l'utente non è ancora autenticato nel contesto di Spring...
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 6. Controlla se il token è valido e non scaduto
                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 7. Salva l'utente autenticato nel contesto
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("Errore validazione JWT: {}", e.getMessage());
        }

        // 8. Passa alla prossima fase (il Controller)
        filterChain.doFilter(request, response);
    }
}