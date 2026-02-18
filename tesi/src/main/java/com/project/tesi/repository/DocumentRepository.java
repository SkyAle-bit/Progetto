package com.project.tesi.repository;

import com.project.tesi.model.Document;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Trova tutti i documenti appartenenti a un cliente specifico
    List<Document> findByOwner(User owner);
}