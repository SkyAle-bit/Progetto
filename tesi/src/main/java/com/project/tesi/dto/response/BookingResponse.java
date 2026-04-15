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

    private Long id;

    private String date;

    private String startTime;

    private String endTime;

    private String professionalName;

    private String clientName;

    private Role professionalRole;

    private String meetingLink;

    private BookingStatus status;

    private boolean canJoin;
}