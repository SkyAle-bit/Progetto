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
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public interface ReviewBuilder {
    ReviewBuilder id(Long id);
    ReviewBuilder client(User client);
    ReviewBuilder professional(User professional);
    ReviewBuilder rating(int rating);
    ReviewBuilder comment(String comment);
    ReviewBuilder createdAt(LocalDateTime createdAt);
    Review build();
}
