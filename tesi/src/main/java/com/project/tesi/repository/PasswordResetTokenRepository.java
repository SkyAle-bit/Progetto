package com.project.tesi.repository;

import com.project.tesi.model.PasswordResetToken;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository per i token di reset password.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /** Trova un token tramite il suo valore UUID. */
    Optional<PasswordResetToken> findByToken(String token);

    /** Elimina tutti i token associati a un utente (pulizia prima di generarne uno nuovo). */
    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.user = :user")
    void deleteByUser(@Param("user") User user);
}
