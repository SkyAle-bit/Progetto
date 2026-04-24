package com.project.tesi.builder.impl;

import com.project.tesi.builder.SubscriptionBuilder;
import com.project.tesi.enums.PaymentFrequency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import com.project.tesi.model.*;


public class SubscriptionBuilderImpl implements SubscriptionBuilder {
    private Long id;
    private User user;
    private Plan plan;
    private PaymentFrequency paymentFrequency;
    private int installmentsPaid;
    private int totalInstallments;
    private LocalDate nextPaymentDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean active;
    private int currentCreditsPT;
    private int currentCreditsNutri;
    private LocalDate lastRenewalDate;

    @Override
    public SubscriptionBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public SubscriptionBuilder user(User user) {
        this.user = user;
        return this;
    }
    @Override
    public SubscriptionBuilder plan(Plan plan) {
        this.plan = plan;
        return this;
    }
    @Override
    public SubscriptionBuilder paymentFrequency(PaymentFrequency paymentFrequency) {
        this.paymentFrequency = paymentFrequency;
        return this;
    }
    @Override
    public SubscriptionBuilder installmentsPaid(int installmentsPaid) {
        this.installmentsPaid = installmentsPaid;
        return this;
    }
    @Override
    public SubscriptionBuilder totalInstallments(int totalInstallments) {
        this.totalInstallments = totalInstallments;
        return this;
    }
    @Override
    public SubscriptionBuilder nextPaymentDate(LocalDate nextPaymentDate) {
        this.nextPaymentDate = nextPaymentDate;
        return this;
    }
    @Override
    public SubscriptionBuilder startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }
    @Override
    public SubscriptionBuilder endDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }
    @Override
    public SubscriptionBuilder active(boolean active) {
        this.active = active;
        return this;
    }
    @Override
    public SubscriptionBuilder currentCreditsPT(int currentCreditsPT) {
        this.currentCreditsPT = currentCreditsPT;
        return this;
    }
    @Override
    public SubscriptionBuilder currentCreditsNutri(int currentCreditsNutri) {
        this.currentCreditsNutri = currentCreditsNutri;
        return this;
    }
    @Override
    public SubscriptionBuilder lastRenewalDate(LocalDate lastRenewalDate) {
        this.lastRenewalDate = lastRenewalDate;
        return this;
    }

    @Override
    public Subscription build() {
        Subscription obj = new Subscription();
        obj.setId(this.id);
        obj.setUser(this.user);
        obj.setPlan(this.plan);
        obj.setPaymentFrequency(this.paymentFrequency);
        obj.setInstallmentsPaid(this.installmentsPaid);
        obj.setTotalInstallments(this.totalInstallments);
        obj.setNextPaymentDate(this.nextPaymentDate);
        obj.setStartDate(this.startDate);
        obj.setEndDate(this.endDate);
        obj.setActive(this.active);
        obj.setCurrentCreditsPT(this.currentCreditsPT);
        obj.setCurrentCreditsNutri(this.currentCreditsNutri);
        obj.setLastRenewalDate(this.lastRenewalDate);
        return obj;
    }
}
