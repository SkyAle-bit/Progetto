package com.project.tesi.builder.impl;

import com.project.tesi.builder.UserBuilder;
import com.project.tesi.enums.Role;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public class UserBuilderImpl implements UserBuilder {
    private Long id;
    private String email;
    private String password;
    private String profilePicture;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private Role role;
    private String professionalBio;
    private User assignedPT;
    private User assignedNutritionist;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public UserBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }
    @Override
    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }
    @Override
    public UserBuilder profilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
        return this;
    }
    @Override
    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }
    @Override
    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    @Override
    public UserBuilder profilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
        return this;
    }
    @Override
    public UserBuilder role(Role role) {
        this.role = role;
        return this;
    }
    @Override
    public UserBuilder professionalBio(String professionalBio) {
        this.professionalBio = professionalBio;
        return this;
    }
    @Override
    public UserBuilder assignedPT(User assignedPT) {
        this.assignedPT = assignedPT;
        return this;
    }
    @Override
    public UserBuilder assignedNutritionist(User assignedNutritionist) {
        this.assignedNutritionist = assignedNutritionist;
        return this;
    }
    @Override
    public UserBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    @Override
    public UserBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    @Override
    public User build() {
        User obj = new User();
        obj.setId(this.id);
        obj.setEmail(this.email);
        obj.setPassword(this.password);
        obj.setProfilePicture(this.profilePicture);
        obj.setFirstName(this.firstName);
        obj.setLastName(this.lastName);
        obj.setProfilePictureUrl(this.profilePictureUrl);
        obj.setRole(this.role);
        obj.setProfessionalBio(this.professionalBio);
        obj.setAssignedPT(this.assignedPT);
        obj.setAssignedNutritionist(this.assignedNutritionist);
        obj.setCreatedAt(this.createdAt);
        obj.setUpdatedAt(this.updatedAt);
        return obj;
    }
}
