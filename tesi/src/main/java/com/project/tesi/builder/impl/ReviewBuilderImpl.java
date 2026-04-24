package com.project.tesi.builder.impl;

import com.project.tesi.builder.ReviewBuilder;
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


public class ReviewBuilderImpl implements ReviewBuilder {
    private Long id;
    private User client;
    private User professional;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    @Override
    public ReviewBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public ReviewBuilder client(User client) {
        this.client = client;
        return this;
    }
    @Override
    public ReviewBuilder professional(User professional) {
        this.professional = professional;
        return this;
    }
    @Override
    public ReviewBuilder rating(int rating) {
        this.rating = rating;
        return this;
    }
    @Override
    public ReviewBuilder comment(String comment) {
        this.comment = comment;
        return this;
    }
    @Override
    public ReviewBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public Review build() {
        Review obj = new Review();
        obj.setId(this.id);
        obj.setClient(this.client);
        obj.setProfessional(this.professional);
        obj.setRating(this.rating);
        obj.setComment(this.comment);
        obj.setCreatedAt(this.createdAt);
        return obj;
    }
}
