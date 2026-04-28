package com.project.tesi.builder;

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
