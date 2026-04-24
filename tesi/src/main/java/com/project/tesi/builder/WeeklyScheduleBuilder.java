package com.project.tesi.builder;

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
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalTime;
import com.project.tesi.model.*;


public interface WeeklyScheduleBuilder {
    WeeklyScheduleBuilder id(Long id);
    WeeklyScheduleBuilder professional(User professional);
    WeeklyScheduleBuilder dayOfWeek(DayOfWeek dayOfWeek);
    WeeklyScheduleBuilder startTime(LocalTime startTime);
    WeeklyScheduleBuilder endTime(LocalTime endTime);
    WeeklySchedule build();
}
