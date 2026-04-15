package com.project.tesi.model;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entità Documento — rappresenta un file caricato sulla piattaforma.
 *
 * I documenti possono essere di vari tipi (definiti in {@link DocumentType}):
 * <ul>
 *   <li><b>WORKOUT_PLAN</b> — scheda di allenamento, caricata dal Personal Trainer</li>
 *   <li><b>DIET_PLAN</b> — piano alimentare, caricato dal Nutrizionista</li>
 *   <li><b>INSURANCE_POLICE</b> — polizza assicurativa</li>
 *   <li><b>MEDICAL_CERT</b> — certificato medico</li>
 * </ul>
 *
 * Il file viene salvato fisicamente su disco (percorso in {@code filePath})
 * e i metadati vengono persistiti nel database.
 */
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

    private String filePath;

    private String contentType;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

    private LocalDateTime uploadDate;

    @Column(columnDefinition = "TEXT")
    private String notes;
}