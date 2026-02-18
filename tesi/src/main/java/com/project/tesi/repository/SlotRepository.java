package com.project.tesi.repository;

import com.project.tesi.model.Slot;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    // Serve al generatore per evitare duplicati
    boolean existsByProfessionalAndStartTime(User professional, LocalDateTime startTime);

    // Query per il Frontend (Calendario): Trova slot liberi in un range di date
    @Query("SELECT s FROM Slot s WHERE s.professional.id = :profId " +
            "AND s.isBooked = false " +
            "AND s.startTime BETWEEN :start AND :end " +
            "ORDER BY s.startTime ASC")
    List<Slot> findAvailableSlots(@Param("profId") Long profId,
                                  @Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    // Serve a SlotServiceImpl per mostrare il calendario
    List<Slot> findByProfessionalAndIsBookedFalse(User professional);

    List<Slot> findByIsBookedFalse();
}