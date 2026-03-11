package com.project.tesi.repository;

import com.project.tesi.model.User;
import com.project.tesi.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository per l'accesso ai dati dell'entità {@link User}.
 *
 * Estende {@link JpaRepository} che fornisce automaticamente le operazioni
 * CRUD di base (findAll, findById, save, delete, ecc.).
 * I metodi aggiuntivi usano le query derivate di Spring Data JPA.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Cerca un utente tramite il suo indirizzo email (usato nel login).
     *
     * @param email l'indirizzo email da cercare
     * @return un Optional contenente l'utente, vuoto se non trovato
     */
    Optional<User> findByEmail(String email);

    /**
     * Restituisce tutti gli utenti con un determinato ruolo (es. tutti i PT).
     *
     * @param role il ruolo da filtrare
     * @return lista degli utenti con quel ruolo
     */
    List<User> findByRole(Role role);

    /**
     * Conta quanti clienti sono attualmente assegnati a un Personal Trainer.
     * Usato per verificare il limite massimo di 10 clienti per professionista.
     *
     * @param pt il Personal Trainer di cui contare i clienti
     * @return numero di clienti assegnati
     */
    long countByAssignedPT(User pt);

    /**
     * Conta quanti clienti sono attualmente assegnati a un Nutrizionista.
     * Usato per verificare il limite massimo di 10 clienti per professionista.
     *
     * @param nutritionist il Nutrizionista di cui contare i clienti
     * @return numero di clienti assegnati
     */
    long countByAssignedNutritionist(User nutritionist);

    /**
     * Restituisce tutti i clienti assegnati a un Personal Trainer.
     *
     * @param pt il Personal Trainer
     * @return lista dei clienti assegnati
     */
    List<User> findByAssignedPT(User pt);

    /**
     * Restituisce tutti i clienti assegnati a un Nutrizionista.
     *
     * @param nutritionist il Nutrizionista
     * @return lista dei clienti assegnati
     */
    List<User> findByAssignedNutritionist(User nutritionist);

    /**
     * Restituisce tutti gli utenti che hanno uno dei ruoli specificati.
     * Usato dallo scheduler per recuperare tutti i professionisti (PT + Nutrizionisti).
     *
     * @param roles lista di ruoli da includere
     * @return lista degli utenti corrispondenti
     */
    List<User> findByRoleIn(List<Role> roles);
}