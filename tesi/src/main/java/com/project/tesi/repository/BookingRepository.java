package com.project.tesi.repository;

import com.project.tesi.model.Booking;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser(User user);

    List<Booking> findByProfessional(User professional);

    boolean existsByUserAndProfessional(User user, User professional);

    // Nuovo metodo per le prenotazioni future ordinate per data
    List<Booking> findByUserAndSlotStartTimeAfterOrderBySlotStartTimeAsc(User user, LocalDateTime start);

    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.slot.startTime > :now ORDER BY b.slot.startTime ASC")
    List<Booking> findFutureByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    // Appuntamenti di oggi per un professionista (range: inizio e fine giorno)
    @Query("SELECT b FROM Booking b WHERE b.professional = :professional AND b.slot.startTime >= :dayStart AND b.slot.startTime < :dayEnd ORDER BY b.slot.startTime ASC")
    List<Booking> findTodayByProfessional(@Param("professional") User professional, @Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd);

    // Prenotazioni recenti per un cliente (ordinate per data creazione)
    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.bookedAt >= :since ORDER BY b.bookedAt DESC")
    List<Booking> findRecentByUser(@Param("user") User user, @Param("since") LocalDateTime since);

    // Prenotazioni recenti per un professionista (ordinate per data creazione)
    @Query("SELECT b FROM Booking b WHERE b.professional = :professional AND b.bookedAt >= :since ORDER BY b.bookedAt DESC")
    List<Booking> findRecentByProfessional(@Param("professional") User professional, @Param("since") LocalDateTime since);
}