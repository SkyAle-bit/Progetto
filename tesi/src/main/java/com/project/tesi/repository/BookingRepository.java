package com.project.tesi.repository;

import com.project.tesi.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Metodo 1: Serve per la Dashboard CLIENTE
    // "Dammi tutte le prenotazioni fatte dall'utente X"
    List<Booking> findByClientId(Long clientId);

    // Metodo 2: Serve per la Dashboard PROFESSIONISTA
    // "Dammi tutte le prenotazioni ricevute dal professionista Y"
    // (Nota: Spring naviga dentro 'slot' per trovare il 'professional')
    List<Booking> findBySlotProfessionalId(Long professionalId);
}