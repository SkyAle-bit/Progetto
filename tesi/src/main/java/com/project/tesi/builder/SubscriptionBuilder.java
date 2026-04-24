package com.project.tesi.builder;

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


public interface SubscriptionBuilder {
    SubscriptionBuilder id(Long id);
    SubscriptionBuilder user(User user);
    SubscriptionBuilder plan(Plan plan);
    SubscriptionBuilder paymentFrequency(PaymentFrequency paymentFrequency);
    SubscriptionBuilder installmentsPaid(int installmentsPaid);
    SubscriptionBuilder totalInstallments(int totalInstallments);
    SubscriptionBuilder nextPaymentDate(LocalDate nextPaymentDate);
    SubscriptionBuilder startDate(LocalDate startDate);
    SubscriptionBuilder endDate(LocalDate endDate);
    SubscriptionBuilder active(boolean active);
    SubscriptionBuilder currentCreditsPT(int currentCreditsPT);
    SubscriptionBuilder currentCreditsNutri(int currentCreditsNutri);
    SubscriptionBuilder lastRenewalDate(LocalDate lastRenewalDate);
    Subscription build();
}
