package com.project.tesi.dto.response;

import com.project.tesi.enums.BookingStatus;
import com.project.tesi.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String date;               // yyyy-MM-dd
    private String startTime;           // HH:mm
    private String endTime;             // HH:mm
    private String professionalName;    // Nome completo del professionista
    private String clientName;          // Nome completo del cliente
    private Role professionalRole;       // Ruolo del professionista
    private String meetingLink;
    private BookingStatus status;
    private boolean canJoin;
}