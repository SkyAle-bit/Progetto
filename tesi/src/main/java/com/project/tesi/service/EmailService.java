package com.project.tesi.service;

import com.project.tesi.dto.request.JobApplicationRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * Interfaccia del servizio per l'invio di email tramite Resend API.
 */
public interface EmailService {

    /** Invia una candidatura lavorativa via email all'admin con il CV allegato. */
    void sendJobApplication(JobApplicationRequest request, MultipartFile cv);

    /** Metodo asincrono interno per l'invio post-elaborazione file. */
    void sendEmailAsync(JobApplicationRequest request, byte[] cvBytes, String cvFileName, String cvContentType);

    /** Invia l'email di benvenuto al nuovo utente registrato. */
    void sendWelcomeEmail(String toEmail, String firstName);

    /**
     * Invia l'email di promemoria per una prenotazione imminente.
     *
     * @param toEmail           email del destinatario
     * @param recipientName     nome del destinatario
     * @param otherPartyName    nome dell'altra parte (cliente o professionista)
     * @param startTime         data/ora dell'appuntamento
     * @param meetingLink       link Jitsi per la videochiamata
     * @param isForClient       true se il destinatario è il cliente, false se è il professionista
     */
    void sendBookingReminderEmail(String toEmail, String recipientName, String otherPartyName,
                                   java.time.LocalDateTime startTime, String meetingLink, boolean isForClient);
}
