package com.project.tesi.builder;

import com.project.tesi.enums.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public interface DocumentBuilder {
    DocumentBuilder id(Long id);
    DocumentBuilder fileName(String fileName);
    DocumentBuilder filePath(String filePath);
    DocumentBuilder contentType(String contentType);
    DocumentBuilder type(DocumentType type);
    DocumentBuilder owner(User owner);
    DocumentBuilder uploadedBy(User uploadedBy);
    DocumentBuilder uploadDate(LocalDateTime uploadDate);
    DocumentBuilder notes(String notes);
    Document build();
}
