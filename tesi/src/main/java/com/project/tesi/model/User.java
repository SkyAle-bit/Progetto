package com.project.tesi.model;

import com.project.tesi.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Solo per professionisti (PT/Nutri)
    @Column(columnDefinition = "TEXT")
    private String professionalBio;

    // --- ASSEGNAZIONE CLIENTI (Max 10 per professionista) ---
    // Questi campi sono valorizzati SOLO se l'utente è un CLIENTE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_pt_id")
    private User assignedPT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_nutritionist_id")
    private User assignedNutritionist;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}