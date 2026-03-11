package com.project.tesi.model;

import com.project.tesi.enums.PlanDuration;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entità Piano di Abbonamento — definisce un'offerta commerciale della piattaforma.
 *
 * Ogni piano specifica:
 * <ul>
 *   <li>La durata (SEMESTRALE o ANNUALE, tramite {@link PlanDuration})</li>
 *   <li>Il prezzo totale in soluzione unica e il prezzo della singola rata mensile</li>
 *   <li>Il numero di crediti mensili per prenotare consulenze con PT e Nutrizionisti</li>
 *   <li>I dettagli della copertura assicurativa inclusa</li>
 * </ul>
 *
 * Il nome del piano è univoco (es. "Gold Annuale", "Silver Semestrale").
 */
@Entity
@Table(name = "plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    /** Identificativo univoco del piano. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nome commerciale del piano (univoco, es. "Gold Annuale"). */
    @Column(nullable = false, unique = true)
    private String name;

    /** Durata del piano: SEMESTRALE (6 mesi) o ANNUALE (12 mesi). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanDuration duration;

    // ── PREZZI ──────────────────────────────────────────────────

    /** Prezzo totale del piano se pagato in un'unica soluzione (€). */
    @Column(nullable = false)
    private Double fullPrice;

    /** Prezzo della singola rata mensile se pagato a rate (€). */
    @Column(nullable = false)
    private Double monthlyInstallmentPrice;

    // ── CREDITI MENSILI ─────────────────────────────────────────

    /** Numero di crediti mensili per prenotare consulenze con il Personal Trainer. */
    private int monthlyCreditsPT;

    /** Numero di crediti mensili per prenotare consulenze con il Nutrizionista. */
    private int monthlyCreditsNutri;

    // ── ASSICURAZIONE ───────────────────────────────────────────

    /** Descrizione testuale della copertura assicurativa inclusa nel piano. */
    private String insuranceCoverageDetails;
}