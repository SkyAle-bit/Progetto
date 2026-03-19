package com.project.tesi.enums;

/**
 * Ruoli disponibili nel sistema.
 * Determina i permessi e le funzionalità accessibili dall'utente.
 */
public enum Role {
    CLIENT,             // Utente finale
    PERSONAL_TRAINER,   // Professionista PT
    NUTRITIONIST,       // Professionista Nutrizionista
    MODERATOR,          // Moderatore operativo
    INSURANCE_MANAGER,  // Gestore polizze
    ADMIN               // Amministratore sistema
}