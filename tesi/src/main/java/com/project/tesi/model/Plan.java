package com.project.tesi.model;

import com.project.tesi.enums.PlanDuration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanDuration duration;


    @Column(nullable = false)
    private Double fullPrice;

    @Column(nullable = false)
    private Double monthlyInstallmentPrice;


    private int monthlyCreditsPT;

    private int monthlyCreditsNutri;


    private String insuranceCoverageDetails;
}