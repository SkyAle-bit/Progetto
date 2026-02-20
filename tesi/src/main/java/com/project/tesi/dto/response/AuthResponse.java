package com.project.tesi.dto.response;

import com.project.tesi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;

    @Builder.Default
    private String type = "Bearer";

    // --- NUOVI CAMPI PER IL FRONTEND ---
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String profilePicture;
}