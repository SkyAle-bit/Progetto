package com.project.tesi.model;

import com.project.tesi.enums.PaymentFrequency;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    // STATO PAGAMENTO
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentFrequency paymentFrequency;

    private int installmentsPaid;   // Rate pagate finora
    private int totalInstallments;  // Totale rate previste (1, 6 o 12)
    private LocalDate nextPaymentDate;

    // DURATA TEMPORALE
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isActive;

    // SALDO CREDITI ATTUALE
    private int currentCreditsPT;
    private int currentCreditsNutri;
    private LocalDate lastRenewalDate; // Data ultimo reset crediti
}