package com.project.tesi.builder.impl;

import com.project.tesi.builder.WeeklyScheduleBuilder;
import java.time.DayOfWeek;
import java.time.LocalTime;
import com.project.tesi.model.*;


public class WeeklyScheduleBuilderImpl implements WeeklyScheduleBuilder {
    private Long id;
    private User professional;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public WeeklyScheduleBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public WeeklyScheduleBuilder professional(User professional) {
        this.professional = professional;
        return this;
    }
    @Override
    public WeeklyScheduleBuilder dayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        return this;
    }
    @Override
    public WeeklyScheduleBuilder startTime(LocalTime startTime) {
        this.startTime = startTime;
        return this;
    }
    @Override
    public WeeklyScheduleBuilder endTime(LocalTime endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    public WeeklySchedule build() {
        WeeklySchedule obj = new WeeklySchedule();
        obj.setId(this.id);
        obj.setProfessional(this.professional);
        obj.setDayOfWeek(this.dayOfWeek);
        obj.setStartTime(this.startTime);
        obj.setEndTime(this.endTime);
        return obj;
    }
}
