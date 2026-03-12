package com.project.tesi.repository;

import com.project.tesi.model.Booking;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository per l'accesso ai dati dell'entità {@link Booking}.
 *
 * Fornisce query personalizzate per recuperare le prenotazioni
 * filtrate per cliente, professionista, data o intervallo temporale.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Restituisce tutte le prenotazioni di un cliente.
     *
     * @param user il cliente
     * @return lista delle prenotazioni
     */
    List<Booking> findByUser(User user);

    /**
     * Restituisce tutte le prenotazioni ricevute da un professionista.
     *
     * @param professional il professionista
     * @return lista delle prenotazioni
     */
    List<Booking> findByProfessional(User professional);

    /**
     * Verifica se esiste già almeno una prenotazione tra un cliente e un professionista.
     *
     * @param user         il cliente
     * @param professional il professionista
     * @return {@code true} se esiste almeno una prenotazione
     */
    boolean existsByUserAndProfessional(User user, User professional);

    /**
     * Restituisce le prenotazioni future di un cliente, ordinate per data di inizio slot crescente.
     *
     * @param user  il cliente
     * @param start data/ora a partire dalla quale cercare (tipicamente LocalDateTime.now())
     * @return lista delle prenotazioni future ordinate cronologicamente
     */
    List<Booking> findByUserAndSlotStartTimeAfterOrderBySlotStartTimeAsc(User user, LocalDateTime start);

    /**
     * Restituisce le prenotazioni future di un cliente (query JPQL esplicita).
     *
     * @param user il cliente
     * @param now  data/ora corrente
     * @return lista delle prenotazioni future
     */
    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.slot.startTime > :now ORDER BY b.slot.startTime ASC")
    List<Booking> findFutureByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Restituisce gli appuntamenti di oggi per un professionista.
     * Filtra gli slot con startTime compreso tra l'inizio e la fine della giornata.
     *
     * @param professional il professionista
     * @param dayStart     inizio giornata (00:00)
     * @param dayEnd       fine giornata (00:00 del giorno successivo)
     * @return lista degli appuntamenti odierni ordinati per orario
     */
    @Query("SELECT b FROM Booking b WHERE b.professional = :professional AND b.slot.startTime >= :dayStart AND b.slot.startTime < :dayEnd ORDER BY b.slot.startTime ASC")
    List<Booking> findTodayByProfessional(@Param("professional") User professional, @Param("dayStart") LocalDateTime dayStart, @Param("dayEnd") LocalDateTime dayEnd);

    /**
     * Restituisce le prenotazioni recenti di un cliente (per il feed attività).
     *
     * @param user  il cliente
     * @param since data/ora minima di creazione
     * @return lista delle prenotazioni recenti
     */
    @Query("SELECT b FROM Booking b WHERE b.user = :user AND b.bookedAt >= :since ORDER BY b.bookedAt DESC")
    List<Booking> findRecentByUser(@Param("user") User user, @Param("since") LocalDateTime since);

    /**
     * Restituisce le prenotazioni recenti ricevute da un professionista (per il feed attività).
     *
     * @param professional il professionista
     * @param since        data/ora minima di creazione
     * @return lista delle prenotazioni recenti
     */
    @Query("SELECT b FROM Booking b WHERE b.professional = :professional AND b.bookedAt >= :since ORDER BY b.bookedAt DESC")
    List<Booking> findRecentByProfessional(@Param("professional") User professional, @Param("since") LocalDateTime since);

    @Modifying
    @Query("DELETE FROM Booking b WHERE b.user.id = :userId OR b.professional.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * Trova le prenotazioni CONFIRMED il cui slot inizia tra {@code from} e {@code to}
     * e per le quali non è ancora stato inviato il promemoria email.
     */
    @Query("SELECT b FROM Booking b " +
           "WHERE b.status = com.project.tesi.enums.BookingStatus.CONFIRMED " +
           "AND b.reminderSent = false " +
           "AND b.slot.startTime >= :from " +
           "AND b.slot.startTime <= :to")
    List<Booking> findUpcomingNeedingReminder(@Param("from") LocalDateTime from,
                                              @Param("to") LocalDateTime to);
}