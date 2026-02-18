package com.project.tesi.dto.response;

import com.project.tesi.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private LocalDateTime startTime;
    private String professionalName; // Con chi è la call
    private String userName; // Chi ha prenotato (utile per il professionista)
    private String meetingLink; // Link Google Meet
    private BookingStatus status;
    private boolean canJoin; // True se mancano <10 min all'inizio
}