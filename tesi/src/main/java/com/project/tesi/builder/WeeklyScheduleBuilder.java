package com.project.tesi.builder;

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
