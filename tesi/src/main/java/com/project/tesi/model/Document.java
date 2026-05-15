package com.project.tesi.model;

import com.project.tesi.builder.DocumentBuilder;
import com.project.tesi.builder.impl.DocumentBuilderImpl;
import com.project.tesi.enums.DocumentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"owner", "uploadedBy"})
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String fileName;
    private String filePath;
    private String contentType;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @ManyToOne
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_document_owner_id"))
    private User owner;

    @ManyToOne
    @JoinColumn(name = "uploaded_by_id", foreignKey = @ForeignKey(name = "fk_document_uploaded_by_id"))
    private User uploadedBy;

    private LocalDateTime uploadDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public static DocumentBuilder builder() {
        return new DocumentBuilderImpl();
    }
}
