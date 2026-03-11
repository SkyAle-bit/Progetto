package com.project.tesi.repository;

import com.project.tesi.model.Slot;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository per l'accesso ai dati dell'entità {@link Slot}.
 *
 * Fornisce query per la gestione del calendario degli appuntamenti:
 * ricerca slot disponibili, verifica duplicati e filtro per professionista.
 */
public interface SlotRepository extends JpaRepository<Slot, Long> {

    /**
     * Verifica se esiste già uno slot per un professionista a un dato orario di inizio.
     * Usato dal generatore automatico per evitare la creazione di slot duplicati.
     *
     * @param professional il professionista
     * @param startTime    l'orario di inizio da verificare
     * @return {@code true} se lo slot esiste già
     */
    boolean existsByProfessionalAndStartTime(User professional, LocalDateTime startTime);

    /**
     * Restituisce gli slot liberi di un professionista in un intervallo di date.
     * Usato dal frontend per mostrare il calendario con le disponibilità.
     *
     * @param profId ID del professionista
     * @param start  data/ora di inizio dell'intervallo
     * @param end    data/ora di fine dell'intervallo
     * @return lista degli slot disponibili, ordinati per orario crescente
     */
    @Query("SELECT s FROM Slot s WHERE s.professional.id = :profId " +
            "AND s.isBooked = false " +
            "AND s.startTime BETWEEN :start AND :end " +
            "ORDER BY s.startTime ASC")
    List<Slot> findAvailableSlots(@Param("profId") Long profId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Restituisce tutti gli slot non prenotati di un professionista.
     *
     * @param professional il professionista
     * @return lista degli slot liberi
     */
    List<Slot> findByProfessionalAndIsBookedFalse(User professional);

    /**
     * Restituisce tutti gli slot (liberi e prenotati) di un professionista.
     *
     * @param professional il professionista
     * @return lista completa degli slot
     */
    List<Slot> findByProfessional(User professional);

    /**
     * Restituisce tutti gli slot liberi del sistema (qualsiasi professionista).
     *
     * @return lista degli slot non ancora prenotati
     */
    List<Slot> findByIsBookedFalse();
}