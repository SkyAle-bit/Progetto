package com.project.tesi.repository;

import com.project.tesi.model.ChatMessage;
import com.project.tesi.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository per l'accesso ai dati dell'entità {@link ChatMessage}.
 *
 * Fornisce query ottimizzate per:
 * <ul>
 *   <li>Recuperare la cronologia dei messaggi tra due utenti (paginata)</li>
 *   <li>Trovare tutti i partner di conversazione di un utente</li>
 *   <li>Contare e aggiornare lo stato dei messaggi non letti</li>
 * </ul>
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Recupera la cronologia dei messaggi tra due utenti, ordinata per data crescente (paginata).
     * Include i messaggi in entrambe le direzioni (A→B e B→A).
     *
     * @param userId1  ID del primo utente
     * @param userId2  ID del secondo utente
     * @param pageable parametri di paginazione
     * @return lista dei messaggi della conversazione
     */
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE (m.sender.id = :uid1 AND m.receiver.id = :uid2) " +
            "   OR (m.sender.id = :uid2 AND m.receiver.id = :uid1) " +
            "ORDER BY m.createdAt ASC")
    List<ChatMessage> findConversation(@Param("uid1") Long userId1,
                                       @Param("uid2") Long userId2,
                                       Pageable pageable);

    /**
     * Restituisce la lista di tutti gli utenti con cui l'utente indicato ha scambiato messaggi.
     * Usato per mostrare la lista delle conversazioni nel pannello chat.
     *
     * @param userId ID dell'utente
     * @return lista dei partner di conversazione (senza duplicati)
     */
    @Query("SELECT DISTINCT u FROM User u WHERE u IN " +
           "(SELECT m.receiver FROM ChatMessage m WHERE m.sender.id = :userId) " +
           "OR u IN " +
           "(SELECT m.sender FROM ChatMessage m WHERE m.receiver.id = :userId)")
    List<User> findConversationPartners(@Param("userId") Long userId);

    /**
     * Recupera l'ultimo messaggio scambiato tra due utenti (per l'anteprima conversazione).
     * Restituisce una lista paginata; tipicamente si richiede solo il primo elemento.
     *
     * @param userId1  ID del primo utente
     * @param userId2  ID del secondo utente
     * @param pageable paginazione (es. PageRequest.of(0, 1))
     * @return lista contenente l'ultimo messaggio (o vuota se non ci sono messaggi)
     */
    @Query("SELECT m FROM ChatMessage m " +
            "WHERE (m.sender.id = :uid1 AND m.receiver.id = :uid2) " +
            "   OR (m.sender.id = :uid2 AND m.receiver.id = :uid1) " +
            "ORDER BY m.createdAt DESC")
    List<ChatMessage> findLastMessages(@Param("uid1") Long userId1, @Param("uid2") Long userId2, Pageable pageable);

    /**
     * Conta i messaggi non ancora letti inviati da un mittente specifico a un destinatario.
     *
     * @param receiverId ID del destinatario
     * @param senderId   ID del mittente
     * @return numero di messaggi non letti
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m " +
            "WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId " +
            "AND m.status <> com.project.tesi.enums.MessageStatus.READ")
    int countUnreadMessages(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

    /**
     * Segna come letti (stato READ) tutti i messaggi ricevuti da un certo mittente.
     * Operazione di aggiornamento batch — richiede {@code @Modifying}.
     *
     * @param receiverId ID del destinatario che sta leggendo
     * @param senderId   ID del mittente i cui messaggi vanno segnati come letti
     * @return numero di record aggiornati
     */
    @Modifying
    @Query("UPDATE ChatMessage m SET m.status = com.project.tesi.enums.MessageStatus.READ " +
            "WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId " +
            "AND m.status <> com.project.tesi.enums.MessageStatus.READ")
    int markMessagesAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

    /**
     * Conta il totale dei messaggi non letti ricevuti da un utente (da qualsiasi mittente).
     * Usato per il badge di notifica globale nella UI.
     *
     * @param userId ID del destinatario
     * @return numero totale di messaggi non letti
     */
    @Query("SELECT COUNT(m) FROM ChatMessage m " +
            "WHERE m.receiver.id = :userId " +
            "AND m.status <> com.project.tesi.enums.MessageStatus.READ")
    int countAllUnreadMessages(@Param("userId") Long userId);
}
