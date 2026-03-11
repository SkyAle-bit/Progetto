package com.project.tesi.security;

import com.project.tesi.enums.Role;
import com.project.tesi.model.User;
import com.project.tesi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Test unitari per {@link CustomUserDetailsService}.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    @DisplayName("loadUserByUsername — utente trovato restituisce UserDetails corretto")
    void loadUserByUsername_success() {
        User user = User.builder().id(1L).email("mario@test.com")
                .password("hashed").role(Role.CLIENT).build();
        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("mario@test.com");

        assertThat(details.getUsername()).isEqualTo("mario@test.com");
        assertThat(details.getPassword()).isEqualTo("hashed");
        assertThat(details.getAuthorities()).hasSize(1);
        assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_CLIENT");
    }

    @Test
    @DisplayName("loadUserByUsername — utente PT restituisce ROLE_PERSONAL_TRAINER")
    void loadUserByUsername_pt() {
        User user = User.builder().id(2L).email("pt@test.com")
                .password("hashed").role(Role.PERSONAL_TRAINER).build();
        when(userRepository.findByEmail("pt@test.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("pt@test.com");

        assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_PERSONAL_TRAINER");
    }

    @Test
    @DisplayName("loadUserByUsername — utente non trovato lancia UsernameNotFoundException")
    void loadUserByUsername_notFound() {
        when(userRepository.findByEmail("nobody@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nobody@test.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nobody@test.com");
    }
}

