package com.project.tesi.repository;

import com.project.tesi.enums.DocumentType;
import com.project.tesi.model.Document;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByOwner(User owner);
    List<Document> findByOwnerAndType(User owner, DocumentType type);
    List<Document> findByOwnerOrderByUploadDateDesc(User owner);
    List<Document> findByOwnerAndTypeOrderByUploadDateDesc(User owner, DocumentType type);
}