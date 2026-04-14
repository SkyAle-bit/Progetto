package com.project.tesi.repository;

import com.project.tesi.model.ChatTermination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository per l'accesso ai dati delle terminazioni chat.
 */
public interface ChatTerminationRepository extends JpaRepository<ChatTermination, Long> {

    /**
     * Verifica se un utente ha terminato la chat con un altro utente.
     */
    boolean existsByTerminatedByIdAndOtherUserId(Long terminatedById, Long otherUserId);

    /**
     * Trova la terminazione di una chat tra due utenti specifici.
     */
    Optional<ChatTermination> findByTerminatedByIdAndOtherUserId(Long terminatedById, Long otherUserId);

    /**
     * Restituisce tutti gli ID degli utenti con cui l'utente ha terminato la chat.
     * Usato per filtrare le conversazioni dalla lista dell'utente.
     */
    @Query("SELECT ct.otherUser.id FROM ChatTermination ct WHERE ct.terminatedBy.id = :userId")
    List<Long> findTerminatedOtherUserIdsByUserId(@Param("userId") Long userId);

    /**
     * Restituisce tutti gli ID degli utenti che hanno terminato la chat con un operatore.
     * Usato dall'admin/moderatore per mostrare il flag "terminata".
     */
    @Query("SELECT ct.terminatedBy.id FROM ChatTermination ct WHERE ct.otherUser.id = :operatorId")
    List<Long> findTerminatedByUserIdsForOperator(@Param("operatorId") Long operatorId);

    /**
     * Elimina la terminazione quando l'utente riapre una nuova chat con lo stesso operatore.
     */
    void deleteByTerminatedByIdAndOtherUserId(Long terminatedById, Long otherUserId);
}
