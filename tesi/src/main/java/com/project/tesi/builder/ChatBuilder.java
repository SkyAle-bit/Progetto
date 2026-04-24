package com.project.tesi.builder;

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


public interface ChatBuilder {
    ChatBuilder id(Long id);
    ChatBuilder user1(User user1);
    ChatBuilder user2(User user2);
    ChatBuilder messages(List<Message> messages);
    ChatBuilder createdAt(LocalDateTime createdAt);
    Chat build();
}
