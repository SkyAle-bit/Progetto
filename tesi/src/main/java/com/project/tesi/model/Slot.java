package com.project.tesi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "slots", indexes = {
        @Index(name = "idx_slot_time", columnList = "startTime"),
        @Index(name = "idx_slot_prof", columnList = "professional_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private User professional;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    private boolean isBooked;

    @Version // Optimistic Locking (Evita doppie prenotazioni)
    private Integer version;
}