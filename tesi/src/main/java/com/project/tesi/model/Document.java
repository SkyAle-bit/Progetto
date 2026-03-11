package com.project.tesi.model;

import com.project.tesi.enums.DocumentType;
import jakarta.persistence.*;
import lombok.*;
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

    /** Identificativo univoco del documento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome originale del file caricato (es. "scheda_allenamento.pdf"). */
    private String fileName;

    /** Percorso assoluto del file su disco (generato con UUID per evitare conflitti). */
    private String filePath;

    /** Tipo MIME del file (es. "application/pdf", "image/png"). */
    private String contentType;

    /** Tipologia del documento nel dominio applicativo. */
    @Enumerated(EnumType.STRING)
    private DocumentType type;

    /** Cliente proprietario del documento (a chi è destinato). */
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    /** Utente che ha fisicamente caricato il documento (PT, Nutrizionista o Admin). */
    @ManyToOne
    @JoinColumn(name = "uploaded_by_id")
    private User uploadedBy;

    /** Data e ora di caricamento del documento. */
    private LocalDateTime uploadDate;

    /** Note testuali aggiuntive del professionista relative al documento. */
    @Column(columnDefinition = "TEXT")
    private String notes;
}