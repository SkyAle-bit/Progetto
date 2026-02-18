package com.project.tesi.repository;

import com.project.tesi.model.WeeklySchedule;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Long> {
    // Recupera tutte le regole orarie di un professionista
    List<WeeklySchedule> findByProfessional(User professional);
}