package com.project.tesi.repository;

import com.project.tesi.model.Booking;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Cerca per Oggetto Utente (consigliato se hai già l'oggetto User caricato)
    List<Booking> findByUser(User user);

    // Cerca per ID Utente (consigliato se hai solo il numero Long dell'ID)
    List<Booking> findByUserId(Long userId);

    // Cerca per Oggetto Professionista
    List<Booking> findByProfessional(User professional);

    // Cerca per ID Professionista
    List<Booking> findByProfessionalId(Long professionalId);

    // Verifica esistenza (per le recensioni)
    boolean existsByUserAndProfessional(User user, User professional);
}