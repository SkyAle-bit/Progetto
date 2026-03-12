package com.project.tesi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitari per {@link JwtUtil}.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // La chiave deve essere almeno 256 bit (32 byte) per HS256
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "dGVzdC1zZWNyZXQta2V5LWxvbmctZW5vdWdoLWZvci1oczI1Ni1hbGdvcml0aG0=");
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 86400000L);
    }

    private UserDetails createUserDetails(String email) {
        return new User(email, "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));
    }

    @Test
    @DisplayName("generateToken — genera un token valido non vuoto")
    void generateToken() {
        UserDetails userDetails = createUserDetails("mario@test.com");
        String token = jwtUtil.generateToken(userDetails);

        assertThat(token).isNotNull().isNotBlank();
        assertThat(token.split("\\.")).hasSize(3); // header.payload.signature
    }

    @Test
    @DisplayName("extractUsername — estrae l'email dal token")
    void extractUsername() {
        UserDetails userDetails = createUserDetails("mario@test.com");
        String token = jwtUtil.generateToken(userDetails);

        String username = jwtUtil.extractUsername(token);

        assertThat(username).isEqualTo("mario@test.com");
    }

    @Test
    @DisplayName("isTokenValid — true per token valido e utente corretto")
    void isTokenValid_true() {
        UserDetails userDetails = createUserDetails("mario@test.com");
        String token = jwtUtil.generateToken(userDetails);

        assertThat(jwtUtil.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid — false per utente diverso")
    void isTokenValid_differentUser() {
        UserDetails original = createUserDetails("mario@test.com");
        UserDetails other = createUserDetails("luca@test.com");
        String token = jwtUtil.generateToken(original);

        assertThat(jwtUtil.isTokenValid(token, other)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid — false per token scaduto")
    void isTokenValid_expired() throws InterruptedException {
        // Crea un JwtUtil con scadenza 1ms
        JwtUtil expiredJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(expiredJwtUtil, "secretKey", "dGVzdC1zZWNyZXQta2V5LWxvbmctZW5vdWdoLWZvci1oczI1Ni1hbGdvcml0aG0=");
        ReflectionTestUtils.setField(expiredJwtUtil, "jwtExpiration", 1L);

        UserDetails userDetails = createUserDetails("mario@test.com");
        String token = expiredJwtUtil.generateToken(userDetails);

        // Attende che il token scada
        Thread.sleep(100);

        // Il token scaduto deve lanciare un'eccezione o restituire false
        try {
            boolean valid = expiredJwtUtil.isTokenValid(token, userDetails);
            assertThat(valid).isFalse();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Accettabile: il token è effettivamente scaduto
            assertThat(e).isNotNull();
        }
    }
}



