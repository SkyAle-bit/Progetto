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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Estrae l'header "Authorization" dalla richiesta
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Se non c'è il token, o non inizia con "Bearer ", lascia passare la richiesta nuda (che verrà poi bloccata)
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

                    // 7. Salva l'utente autenticato nel contesto (lo fa "entrare" nel server)
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Se il token è falso, scaduto o malformato, l'errore viene catturato e la richiesta fallirà
            System.out.println("Errore validazione JWT: " + e.getMessage());
        }

        // 8. Passa alla prossima fase (il Controller)
        filterChain.doFilter(request, response);
    }
}