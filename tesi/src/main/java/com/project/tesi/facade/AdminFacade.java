package com.project.tesi.facade;

import com.project.tesi.service.AdminService;
import com.project.tesi.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Facade per il pannello amministrativo (Design Pattern Facade).
 *
 * Fornisce un punto d'accesso unificato a tutte le operazioni admin,
 * coordinando {@link AdminService} (operazioni CRUD) e {@link AdminStatsService} (statistiche).
 * I controller admin comunicano esclusivamente con questa facade,
 * senza conoscere i servizi sottostanti.
 */
@Component
@RequiredArgsConstructor
public class AdminFacade {

    /** Servizio per le operazioni CRUD su utenti, abbonamenti e piani. */
    private final AdminService adminService;

    /** Servizio per il calcolo delle statistiche aggregate del pannello admin. */
    private final AdminStatsService adminStatsService;

    // ── UTENTI ──────────────────────────────────────────────────

    /** Restituisce la lista di tutti gli utenti registrati nel sistema. */
    public List<Map<String, Object>> getAllUsers() {
        return adminService.getAllUsers();
    }

    /** Crea un nuovo utente (professionista o cliente) con i dati specificati. */
    public Map<String, Object> createUser(Map<String, Object> body) {
        return adminService.createUser(body);
    }

    /** Elimina un utente e tutte le sue entità collegate (documenti, abbonamento). */
    public void deleteUser(Long id) {
        adminService.deleteUser(id);
    }

    // ── ABBONAMENTI ─────────────────────────────────────────────

    /** Restituisce la lista di tutti gli abbonamenti (attivi e scaduti). */
    public List<Map<String, Object>> getAllSubscriptions() {
        return adminService.getAllSubscriptions();
    }

    /** Aggiorna i crediti PT e Nutrizionista di un abbonamento. */
    public Map<String, Object> updateSubscriptionCredits(Long id, int pt, int nutri) {
        return adminService.updateSubscriptionCredits(id, pt, nutri);
    }

    // ── PIANI ───────────────────────────────────────────────────

    /** Crea un nuovo piano commerciale con i dati specificati. */
    public Map<String, Object> createPlan(Map<String, Object> body) {
        return adminService.createPlan(body);
    }

    /** Elimina un piano commerciale dal sistema. */
    public void deletePlan(Long id) {
        adminService.deletePlan(id);
    }

    // ── STATISTICHE ─────────────────────────────────────────────

    /** Restituisce le statistiche aggregate per la dashboard admin (utenti, fatturato, crediti, ecc.). */
    public Map<String, Object> getAdminStats() {
        return adminStatsService.getAdminStats();
    }
}
