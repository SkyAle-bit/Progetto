package com.project.tesi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

/**
 * Configurazione della sicurezza dell'applicazione tramite Spring Security.
 *
 * Definisce:
 * <ul>
 *   <li>Politica CORS per i domini frontend consentiti</li>
 *   <li>Disabilitazione CSRF (API stateless con JWT)</li>
 *   <li>Endpoint pubblici (autenticazione, WebSocket, piani, professionisti, Swagger)</li>
 *   <li>Gestione sessione STATELESS (nessuna sessione HTTP, solo JWT)</li>
 *   <li>Inserimento del filtro JWT prima del filtro standard di autenticazione</li>
 *   <li>PasswordEncoder (attualmente NoOp per sviluppo)</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la catena di filtri di sicurezza HTTP.
     *
     * @param http          il builder di configurazione HTTP
     * @param jwtAuthFilter il filtro JWT da inserire nella catena
     * @return la catena di filtri configurata
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthFilter) throws Exception {
        http
                // Configurazione CORS: consente richieste dai domini frontend
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                            "http://localhost:4200",
                            "https://progetto-fe.vercel.app",
                            "https://backend-tesi-l6ca.onrender.com"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // Disabilita CSRF (non necessario con JWT stateless)
                .csrf(AbstractHttpConfigurer::disable)
                // Definizione degli endpoint pubblici e protetti
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/plans/**").permitAll()
                        .requestMatchers("/api/professionals/**").permitAll()
                        .requestMatchers("/api/reviews/professional/**").permitAll()
                        .requestMatchers("/api/bookings/migrate-meet").permitAll()
                        .requestMatchers("/api/bookings/reset-database").permitAll()
                        .requestMatchers("/api/job-applications/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**")
                        .permitAll()
                        .anyRequest().authenticated())
                // Sessione STATELESS: nessun cookie di sessione, solo JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Inserisce il filtro JWT prima del filtro standard username/password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Espone l'AuthenticationManager come bean per l'iniezione nel servizio di login.
     *
     * @param config la configurazione di autenticazione di Spring
     * @return l'AuthenticationManager configurato
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Definisce il PasswordEncoder da usare per la verifica delle password.
     * Attualmente usa NoOpPasswordEncoder (password in chiaro) per sviluppo.
     * Da sostituire con BCryptPasswordEncoder in produzione.
     *
     * @return il PasswordEncoder
     */
    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
