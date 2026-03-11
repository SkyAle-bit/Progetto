package com.project.tesi.model;

import com.project.tesi.enums.Role;
import jakarta.persistence.*;
import lombok.*;
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

    /** Identificativo univoco dell'utente, generato automaticamente dal database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Indirizzo email univoco, usato come username per il login. */
    @Column(nullable = false, unique = true)
    private String email;

    /** Password dell'utente, salvata in formato hash (BCrypt). */
    @Column(nullable = false)
    private String password;

    /** Immagine del profilo codificata in Base64 (campo TEXT per supportare stringhe lunghe). */
    @Column(columnDefinition = "TEXT")
    private String profilePicture;

    /** Nome dell'utente. */
    private String firstName;

    /** Cognome dell'utente. */
    private String lastName;

    /** URL esterno dell'immagine profilo (alternativo a {@code profilePicture}). */
    private String profilePictureUrl;

    /** Ruolo dell'utente nel sistema (CLIENT, PERSONAL_TRAINER, NUTRITIONIST, ADMIN, INSURANCE_MANAGER). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** Biografia del professionista, visibile nella vetrina pubblica. Valorizzato solo per PT e Nutrizionisti. */
    @Column(columnDefinition = "TEXT")
    private String professionalBio;

    // ── ASSEGNAZIONE PROFESSIONISTI (solo per CLIENT) ───────────

    /** Personal Trainer assegnato al cliente. {@code null} se l'utente non è un CLIENT. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_pt_id")
    private User assignedPT;

    /** Nutrizionista assegnato al cliente. {@code null} se l'utente non è un CLIENT. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_nutritionist_id")
    private User assignedNutritionist;

    // ── TIMESTAMP AUTOMATICI ────────────────────────────────────

    /** Data e ora di creazione dell'account (impostata automaticamente, non modificabile). */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Data e ora dell'ultimo aggiornamento del profilo (impostata automaticamente a ogni modifica). */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}