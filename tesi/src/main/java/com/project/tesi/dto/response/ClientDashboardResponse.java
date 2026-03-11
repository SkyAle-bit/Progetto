package com.project.tesi.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * DTO di risposta per la dashboard del cliente.
 * Aggrega tutte le informazioni necessarie alla pagina principale
 * del cliente in un'unica chiamata API.
 */
@Data
@Builder
public class ClientDashboardResponse {

    /** Profilo completo dell'utente. */
    private UserResponse profile;

    /** Lista dei professionisti assegnati al cliente (PT e/o Nutrizionista). */
    private List<ProfessionalSummaryDTO> followingProfessionals;

    /** Stato dell'abbonamento attivo (crediti residui, scadenza, ecc.). */
    private SubscriptionResponse subscription;

    /** Lista dei prossimi appuntamenti ordinati cronologicamente. */
    private List<BookingResponse> upcomingBookings;
}