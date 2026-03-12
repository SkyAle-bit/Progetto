package com.project.tesi.controller;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.service.DatabaseInitializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller REST per la gestione delle prenotazioni.
 * Permette al cliente di prenotare uno slot con un professionista.
 * Include un endpoint di utility per il reset del database (solo sviluppo).
 * Delega alla {@link UserFacade} (pattern Facade).
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final UserFacade userFacade;
    private final DatabaseInitializerService databaseInitializerService;

    /** Crea una nuova prenotazione per lo slot indicato. */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(userFacade.createBooking(request));
    }

    /** Svuota e ripopola il database con i dati di test (solo per sviluppo). */
    @GetMapping("/reset-database")
    public ResponseEntity<String> resetDatabase() {
        databaseInitializerService.initialize();
        return ResponseEntity.ok("Database svuotato e ripopolato con i nuovi dati di test/link Jitsi!");
    }
}