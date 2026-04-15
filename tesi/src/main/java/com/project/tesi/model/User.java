package com.project.tesi.model;

import com.project.tesi.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * Entità Utente — rappresenta qualsiasi utente del sistema.
 *
 * A seconda del {@link Role} assegnato, un utente può essere:
 * <ul>
 *   <li><b>CLIENT</b> — il cliente che prenota consulenze e riceve documenti</li>
 *   <li><b>PERSONAL_TRAINER</b> — il professionista che offre schede di allenamento</li>
 *   <li><b>NUTRITIONIST</b> — il professionista che offre piani alimentari</li>
 *   <li><b>ADMIN</b> — l'amministratore della piattaforma</li>
 *   <li><b>INSURANCE_MANAGER</b> — il responsabile delle polizze assicurative</li>
 * </ul>
 *
 * I campi {@code assignedPT} e {@code assignedNutritionist} sono valorizzati
 * solo per gli utenti con ruolo CLIENT e indicano i professionisti a cui
 * il cliente è attualmente assegnato (massimo 10 clienti per professionista).
 */
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

    @Column(columnDefinition = "TEXT")
    private String profilePicture;

    private String firstName;

    private String lastName;

    private String profilePictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String professionalBio;


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