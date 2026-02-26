package com.project.tesi.controller;

import com.project.tesi.dto.request.BookingRequest;
import com.project.tesi.dto.response.BookingResponse;
import com.project.tesi.service.BookingService;
import com.project.tesi.service.DatabaseInitializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final DatabaseInitializerService databaseInitializerService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping("/migrate-meet")
    public ResponseEntity<String> migrateFakeMeetLinks() {
        int updated = bookingService.migrateFakeMeetLinks();
        return ResponseEntity.ok("Migrazione completata. Link Google Meet aggiornati: " + updated);
    }

    @GetMapping("/reset-database")
    public ResponseEntity<String> resetDatabase() {
        databaseInitializerService.initialize();
        return ResponseEntity.ok("Database svuotato e ripopolato con i nuovi dati di test/link Jitsi!");
    }
}