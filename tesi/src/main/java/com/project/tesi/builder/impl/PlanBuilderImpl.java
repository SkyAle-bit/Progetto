package com.project.tesi.builder.impl;

import com.project.tesi.builder.PlanBuilder;
import com.project.tesi.enums.PlanDuration;
import com.project.tesi.model.*;


public class PlanBuilderImpl implements PlanBuilder {
    private Long id;
    private String name;
    private PlanDuration duration;
    private Double fullPrice;
    private Double monthlyInstallmentPrice;
    private int monthlyCreditsPT;
    private int monthlyCreditsNutri;
    private String insuranceCoverageDetails;

    @Override
    public PlanBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public PlanBuilder name(String name) {
        this.name = name;
        return this;
    }
    @Override
    public PlanBuilder duration(PlanDuration duration) {
        this.duration = duration;
        return this;
    }
    @Override
    public PlanBuilder fullPrice(Double fullPrice) {
        this.fullPrice = fullPrice;
        return this;
    }
    @Override
    public PlanBuilder monthlyInstallmentPrice(Double monthlyInstallmentPrice) {
        this.monthlyInstallmentPrice = monthlyInstallmentPrice;
        return this;
    }
    @Override
    public PlanBuilder monthlyCreditsPT(int monthlyCreditsPT) {
        this.monthlyCreditsPT = monthlyCreditsPT;
        return this;
    }
    @Override
    public PlanBuilder monthlyCreditsNutri(int monthlyCreditsNutri) {
        this.monthlyCreditsNutri = monthlyCreditsNutri;
        return this;
    }
    @Override
    public PlanBuilder insuranceCoverageDetails(String insuranceCoverageDetails) {
        this.insuranceCoverageDetails = insuranceCoverageDetails;
        return this;
    }

    @Override
    public Plan build() {
        Plan obj = new Plan();
        obj.setId(this.id);
        obj.setName(this.name);
        obj.setDuration(this.duration);
        obj.setFullPrice(this.fullPrice);
        obj.setMonthlyInstallmentPrice(this.monthlyInstallmentPrice);
        obj.setMonthlyCreditsPT(this.monthlyCreditsPT);
        obj.setMonthlyCreditsNutri(this.monthlyCreditsNutri);
        obj.setInsuranceCoverageDetails(this.insuranceCoverageDetails);
        return obj;
    }
}
