package com.project.tesi.builder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public interface PasswordResetTokenBuilder {
    PasswordResetTokenBuilder id(Long id);
    PasswordResetTokenBuilder token(String token);
    PasswordResetTokenBuilder user(User user);
    PasswordResetTokenBuilder expiryDate(LocalDateTime expiryDate);
    PasswordResetTokenBuilder used(boolean used);
    PasswordResetToken build();
}
