package com.project.tesi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "weekly_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private User professional;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek; // Es. MONDAY

    @Column(nullable = false)
    private LocalTime startTime; // Es. 09:00

    @Column(nullable = false)
    private LocalTime endTime;   // Es. 13:00
}