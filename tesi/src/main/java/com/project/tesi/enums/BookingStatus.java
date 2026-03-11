package com.project.tesi.enums;

/**
 * Stato di una prenotazione nel suo ciclo di vita.
 */
public enum BookingStatus {
    CONFIRMED,  // Prenotato
    CANCELED,   // Cancellato
    COMPLETED   // Lezione avvenuta (utile per le recensioni)
}