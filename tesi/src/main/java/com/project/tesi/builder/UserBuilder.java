package com.project.tesi.builder;

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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public interface UserBuilder {
    UserBuilder id(Long id);
    UserBuilder email(String email);
    UserBuilder password(String password);
    UserBuilder profilePicture(String profilePicture);
    UserBuilder firstName(String firstName);
    UserBuilder lastName(String lastName);
    UserBuilder profilePictureUrl(String profilePictureUrl);
    UserBuilder role(Role role);
    UserBuilder professionalBio(String professionalBio);
    UserBuilder assignedPT(User assignedPT);
    UserBuilder assignedNutritionist(User assignedNutritionist);
    UserBuilder createdAt(LocalDateTime createdAt);
    UserBuilder updatedAt(LocalDateTime updatedAt);
    User build();
}
