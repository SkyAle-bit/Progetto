package com.project.tesi.builder;

import com.project.tesi.enums.PlanDuration;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.project.tesi.model.*;


public interface PlanBuilder {
    PlanBuilder id(Long id);
    PlanBuilder name(String name);
    PlanBuilder duration(PlanDuration duration);
    PlanBuilder fullPrice(Double fullPrice);
    PlanBuilder monthlyInstallmentPrice(Double monthlyInstallmentPrice);
    PlanBuilder monthlyCreditsPT(int monthlyCreditsPT);
    PlanBuilder monthlyCreditsNutri(int monthlyCreditsNutri);
    PlanBuilder insuranceCoverageDetails(String insuranceCoverageDetails);
    Plan build();
}
