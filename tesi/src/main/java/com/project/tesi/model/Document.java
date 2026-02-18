package com.project.tesi.model;

import com.project.tesi.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String filePath; // Percorso su disco
    private String contentType; // es. "application/pdf"

    @Enumerated(EnumType.STRING)
    private DocumentType type; // DIETA, SCHEDA, POLIZZA

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; // Di chi è il documento (Cliente)

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy; // Chi lo ha caricato (PT, Nutrizionista, Admin)

    private LocalDateTime uploadDate;
}