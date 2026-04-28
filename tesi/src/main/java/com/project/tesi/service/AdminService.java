package com.project.tesi.service;

import com.project.tesi.dto.request.PlanCreateRequestDTO;
import com.project.tesi.dto.request.UserCreateRequestDTO;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;

import java.util.List;
import java.util.Map;

/**
 * Interfaccia del servizio per le operazioni CRUD di amministrazione.
 * Gestisce utenti, abbonamenti e piani commerciali.
 *
 * I metodi restituiscono entità di dominio tipizzate (User, Subscription, Plan)
 * anziché Map&lt;String, Object&gt; per garantire type-safety a compile-time.
 */
public interface AdminService {

    /** Restituisce la lista di tutti gli utenti registrati. */
    List<User> getAllUsers();

    /** Restituisce gli utenti gestibili dal moderatore. */
    List<User> getModeratorManageableUsers();

    /** Restituisce i contatti chat per il moderatore (Admin e Insurance Manager). */
    List<User> getModeratorChatContacts();

    /** Crea un nuovo utente con i dati specificati. */
    User createUser(UserCreateRequestDTO request);

    /** Crea un nuovo utente come moderatore (solo ruoli consentiti). */
    User createUserAsModerator(UserCreateRequestDTO request);

    /** Aggiorna un utente come moderatore (solo ruoli consentiti). */
    User updateUserAsModerator(Long id, Map<String, Object> body);

    /** Elimina un utente e le sue entità collegate. */
    void deleteUser(Long id);

    /** Elimina un utente come moderatore (solo ruoli consentiti). */
    void deleteUserAsModerator(Long id);

    /** Restituisce la lista di tutti gli abbonamenti. */
    List<Subscription> getAllSubscriptions();

    /** Aggiorna i crediti PT e Nutrizionista di un abbonamento. */
    Subscription updateSubscriptionCredits(Long subscriptionId, int creditsPT, int creditsNutri);

    /** Crea un nuovo piano commerciale. */
    Plan createPlan(PlanCreateRequestDTO request);

    /** Elimina un piano commerciale. */
    void deletePlan(Long id);
}
