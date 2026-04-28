package com.project.tesi.builder.impl;

import com.project.tesi.builder.PasswordResetTokenBuilder;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public class PasswordResetTokenBuilderImpl implements PasswordResetTokenBuilder {
    private Long id;
    private String token;
    private User user;
    private LocalDateTime expiryDate;
    private boolean used= false;

    @Override
    public PasswordResetTokenBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public PasswordResetTokenBuilder token(String token) {
        this.token = token;
        return this;
    }
    @Override
    public PasswordResetTokenBuilder user(User user) {
        this.user = user;
        return this;
    }
    @Override
    public PasswordResetTokenBuilder expiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }
    @Override
    public PasswordResetTokenBuilder used(boolean used) {
        this.used = used;
        return this;
    }

    @Override
    public PasswordResetToken build() {
        PasswordResetToken obj = new PasswordResetToken();
        obj.setId(this.id);
        obj.setToken(this.token);
        obj.setUser(this.user);
        obj.setExpiryDate(this.expiryDate);
        obj.setUsed(this.used);
        return obj;
    }
}
