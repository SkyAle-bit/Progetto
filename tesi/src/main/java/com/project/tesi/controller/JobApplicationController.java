package com.project.tesi.controller;

import com.project.tesi.dto.request.JobApplicationRequest;
import com.project.tesi.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final EmailService emailService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitApplication(
            @RequestPart("data") @Valid JobApplicationRequest request,
            @RequestPart(value = "cv", required = false) MultipartFile cv) {

        // Validazione tipo file (solo PDF)
        if (cv != null && !cv.isEmpty()) {
            String contentType = cv.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Il CV deve essere in formato PDF."));
            }
        }

        emailService.sendJobApplication(request, cv);

        return ResponseEntity.ok(Map.of("message", "Candidatura inviata con successo! Verrai contattato a breve."));
    }
}
