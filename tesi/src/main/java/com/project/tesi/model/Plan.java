package com.project.tesi.model;

import com.project.tesi.builder.PlanBuilder;
import com.project.tesi.builder.impl.PlanBuilderImpl;
import com.project.tesi.enums.PlanDuration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "plans", uniqueConstraints = {
        @UniqueConstraint(name = "uq_plan_name", columnNames = {"name"})
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
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

    public static PlanBuilder builder() {
        return new PlanBuilderImpl();
    }
}
