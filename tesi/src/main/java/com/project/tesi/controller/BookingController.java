package com.project.tesi.controller;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.service.DatabaseInitializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controller REST per la gestione delle prenotazioni.
 * Permette al cliente di prenotare e annullare uno slot con un professionista.
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

    /** Annulla una prenotazione esistente, liberando lo slot e riaccreditando il credito. */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long id, @RequestParam Long userId) {
        userFacade.cancelBooking(id, userId);
        return ResponseEntity.ok(Map.of("message", "Prenotazione annullata con successo. Lo slot è stato liberato e il credito riaccreditato."));
    }

    /** Svuota e ripopola il database con i dati di test (solo per sviluppo). */
    @GetMapping("/reset-database")
    public ResponseEntity<Map<String, String>> resetDatabase() {
        databaseInitializerService.initialize();
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Database svuotato e ripopolato con i nuovi dati di test/link Jitsi!"));
    }
}