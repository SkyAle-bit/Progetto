package com.project.tesi.controller;

import com.project.tesi.dto.request.JobApplicationRequest;
import com.project.tesi.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Controller REST per l'invio di candidature lavorative.
 * Permette a potenziali professionisti di candidarsi come PT o Nutrizionisti,
 * allegando il proprio CV in formato PDF. L'email viene inviata all'amministratore.
 * La validazione del formato PDF è delegata all'{@link EmailService}.
 */
@RestController
@RequestMapping("/api/job-applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final EmailService emailService;

    /**
     * Invia una candidatura lavorativa con dati e CV allegato.
     *
     * @param request dati della candidatura (nome, cognome, email, ruolo, messaggio)
     * @param cv      curriculum vitae in formato PDF (opzionale)
     * @return messaggio di conferma
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> submitApplication(
            @RequestPart("data") @Valid JobApplicationRequest request,
            @RequestPart(value = "cv", required = false) MultipartFile cv) {

        emailService.sendJobApplication(request, cv);

        return ResponseEntity.ok(Map.of("message", "Candidatura inviata con successo! Verrai contattato a breve."));
    }
}
