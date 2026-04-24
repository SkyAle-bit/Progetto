package com.project.tesi.builder.impl;

import com.project.tesi.builder.ChatBuilder;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import com.project.tesi.model.*;


public class ChatBuilderImpl implements ChatBuilder {
    private Long id;
    private User user1;
    private User user2;
    private List<Message> messages= new ArrayList<>();
    private LocalDateTime createdAt;

    @Override
    public ChatBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public ChatBuilder user1(User user1) {
        this.user1 = user1;
        return this;
    }
    @Override
    public ChatBuilder user2(User user2) {
        this.user2 = user2;
        return this;
    }
    @Override
    public ChatBuilder messages(List<Message> messages) {
        this.messages = messages;
        return this;
    }
    @Override
    public ChatBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    @Override
    public Chat build() {
        Chat obj = new Chat();
        obj.setId(this.id);
        obj.setUser1(this.user1);
        obj.setUser2(this.user2);
        obj.setMessages(this.messages);
        obj.setCreatedAt(this.createdAt);
        return obj;
    }
}
