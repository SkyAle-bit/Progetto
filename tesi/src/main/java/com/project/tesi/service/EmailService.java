package com.project.tesi.service;

import com.project.tesi.dto.request.JobApplicationRequest;
import org.springframework.web.multipart.MultipartFile;

public interface EmailService {

    /**
     * Invia una candidatura lavorativa via email all'admin con il CV allegato.
     *
     * @param request i dati del form di candidatura
     * @param cv      il file CV in formato PDF (opzionale)
     */
    void sendJobApplication(JobApplicationRequest request, MultipartFile cv);

    /**
     * Metodo asincrono interno per l'invio post-elaborazione file.
     * È esposto nell'interfaccia per poterne beneficiare tramite AOP (proxy) e
     * Self-Injection.
     */
    void sendEmailAsync(JobApplicationRequest request, byte[] cvBytes, String cvFileName, String cvContentType);
}
