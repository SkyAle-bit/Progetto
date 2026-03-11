package com.project.tesi.dto.response;

import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO di risposta per una prenotazione.
 * Usato nella dashboard del cliente per mostrare gli appuntamenti futuri
 * e nella dashboard del professionista per gli appuntamenti di oggi.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    /** ID della prenotazione. */
    private Long id;

    /** Data dell'appuntamento (formato yyyy-MM-dd). */
    private String date;

    /** Ora di inizio (formato HH:mm). */
    private String startTime;

    /** Ora di fine (formato HH:mm). */
    private String endTime;

    /** Nome completo del professionista. */
    private String professionalName;

    /** Nome completo del cliente. */
    private String clientName;

    /** Ruolo del professionista (PERSONAL_TRAINER o NUTRITIONIST). */
    private Role professionalRole;

    /** Link alla videochiamata Jitsi Meet. */
    private String meetingLink;

    /** Stato corrente della prenotazione. */
    private BookingStatus status;

    /** Indica se l'utente può accedere alla videochiamata (true se mancano meno di 15 minuti). */
    private boolean canJoin;
}