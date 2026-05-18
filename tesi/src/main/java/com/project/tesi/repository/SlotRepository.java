package com.project.tesi.repository;

import com.project.tesi.model.Slot;
import com.project.tesi.model.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    boolean existsByProfessionalAndStartTime(User professional, LocalDateTime startTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Slot s WHERE s.id = :id")
    Optional<Slot> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT s FROM Slot s WHERE s.professional.id = :profId " +
            "AND s.bookedBy IS NULL " +
            "AND s.startTime BETWEEN :start AND :end " +
            "ORDER BY s.startTime ASC")
    List<Slot> findAvailableSlots(@Param("profId") Long profId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    List<Slot> findByProfessionalAndBookedByIsNull(User professional);

    List<Slot> findByProfessional(User professional);

    @Modifying
    @Query("DELETE FROM Slot s WHERE s.professional.id = :profId")
    void deleteByProfessionalId(@Param("profId") Long profId);
}
