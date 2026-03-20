package com.project.tesi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.LocalDateTime;

/**
 * Token per il reset della password.
 * Ogni token ha una scadenza di 30 minuti e può essere usato una sola volta.
 */
@Entity
@Table(name = "password_reset_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Token UUID univoco inviato via email. */
    @Column(nullable = false, unique = true)
    private String token;

    /** Utente a cui appartiene il token. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Data/ora di scadenza del token. */
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    /** Indica se il token è già stato utilizzato. */
    @Builder.Default
    private boolean used = false;

    /** Verifica se il token è scaduto. */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
