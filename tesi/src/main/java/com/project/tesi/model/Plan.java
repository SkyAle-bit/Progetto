package com.project.tesi.model;

import com.project.tesi.enums.PlanDuration;
import jakarta.persistence.*;
import lombok.*;

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
    private String name; // Es. "Gold Annuale"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanDuration duration;

    // PREZZI
    @Column(nullable = false)
    private Double fullPrice; // Prezzo totale (Unica Soluzione)

    @Column(nullable = false)
    private Double monthlyInstallmentPrice; // Prezzo singola rata (Rateizzato)

    // CREDITI MENSILI
    private int monthlyCreditsPT;
    private int monthlyCreditsNutri;

    // INFO ASSICURAZIONE
    private String insuranceCoverageDetails;
}