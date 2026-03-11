package com.project.tesi.repository;

import com.project.tesi.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository per l'accesso ai dati dell'entità {@link Plan}.
 *
 * Fornisce le operazioni CRUD standard più la ricerca per nome univoco del piano.
 */
@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    /**
     * Cerca un piano di abbonamento tramite il suo nome univoco.
     * Usato per verificare che non esistano duplicati alla creazione.
     *
     * @param name il nome del piano (es. "Gold Annuale")
     * @return un Optional contenente il piano, vuoto se non trovato
     */
    Optional<Plan> findByName(String name);
}