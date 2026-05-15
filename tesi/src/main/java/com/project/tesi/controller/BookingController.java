package com.project.tesi.controller;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.model.User;
import com.project.tesi.service.DatabaseInitializerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint REST per le prenotazioni. Permette di gestire appuntamenti e include una route di utilità per il reset del database locale.
 */
@Slf4j
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final UserFacade userFacade;
    private final DatabaseInitializerService databaseInitializerService;

    /** Crea una nuova prenotazione per lo slot indicato. L'ID cliente viene estratto dal token JWT. */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request,
                                                          @AuthenticationPrincipal User user) {
        log.info("Richiesta prenotazione slot {} da utente {}", request.slotId(), user.getId());
        BookingResponse response = userFacade.createBooking(request, user.getId());
        log.info("Prenotazione confermata: id={} utente={}", response.getId(), user.getId());
        return ResponseEntity.ok(response);
    }

    /** Annulla una prenotazione esistente, liberando lo slot e riaccreditando il credito. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelBooking(@PathVariable Long id,
                                                              @AuthenticationPrincipal User user) {
        log.info("Annullamento prenotazione id={} richiesto da utente {}", id, user.getId());
        userFacade.cancelBooking(id, user.getId());
        return ResponseEntity.ok(Map.of("message", "Prenotazione annullata con successo. Lo slot è stato liberato e il credito riaccreditato."));
    }

    /** Svuota e ripopola il database con i dati di test (solo per sviluppo). */
    @GetMapping("/reset-database")
    public ResponseEntity<Map<String, String>> resetDatabase() {
        databaseInitializerService.initialize();
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Database svuotato e ripopolato con i nuovi dati di test/link Jitsi!"));
    }
}
