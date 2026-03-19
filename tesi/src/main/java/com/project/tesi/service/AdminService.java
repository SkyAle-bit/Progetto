package com.project.tesi.service;

import java.util.List;
import java.util.Map;

/**
 * Interfaccia del servizio per le operazioni CRUD di amministrazione.
 * Gestisce utenti, abbonamenti e piani commerciali.
 */
public interface AdminService {

    /** Restituisce la lista di tutti gli utenti registrati. */
    List<Map<String, Object>> getAllUsers();

    /** Restituisce gli utenti gestibili dal moderatore. */
    List<Map<String, Object>> getModeratorManageableUsers();

    /** Crea un nuovo utente con i dati specificati. */
    Map<String, Object> createUser(Map<String, Object> body);

    /** Crea un nuovo utente come moderatore (solo ruoli consentiti). */
    Map<String, Object> createUserAsModerator(Map<String, Object> body);

    /** Aggiorna un utente come moderatore (solo ruoli consentiti). */
    Map<String, Object> updateUserAsModerator(Long id, Map<String, Object> body);

    /** Elimina un utente e le sue entità collegate. */
    void deleteUser(Long id);

    /** Elimina un utente come moderatore (solo ruoli consentiti). */
    void deleteUserAsModerator(Long id);

    /** Restituisce la lista di tutti gli abbonamenti. */
    List<Map<String, Object>> getAllSubscriptions();

    /** Crea un nuovo piano commerciale. */
    Map<String, Object> createPlan(Map<String, Object> body);

    /** Elimina un piano commerciale. */
    void deletePlan(Long id);
}
