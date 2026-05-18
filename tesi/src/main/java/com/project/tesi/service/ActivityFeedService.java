package com.project.tesi.service;

import com.project.tesi.dto.response.ActivityFeedItemResponse;
import com.project.tesi.model.Booking;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface ActivityFeedService {

    List<ActivityFeedItemResponse> getActivityFeed(@NotNull Long userId, int days, int limit);

    /** Chiamato in modo sincrono dentro la transazione di creazione prenotazione. */
    void logBookingCreated(@Valid Booking booking);

    /** Eseguito in modo asincrono su emailTaskExecutor dopo l'upload di un documento. */
    @Async("emailTaskExecutor")
    void logDocumentUploaded(Long clientId, Long uploaderId, String type);
}
