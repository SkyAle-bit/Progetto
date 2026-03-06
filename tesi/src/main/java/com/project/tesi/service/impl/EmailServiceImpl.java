package com.project.tesi.service.impl;

import com.project.tesi.dto.request.JobApplicationRequest;
import com.project.tesi.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final String resendApiKey;
    private final String resendFrom;
    private final String adminEmail;
    private final EmailService self;
    private final RestTemplate restTemplate;

    public EmailServiceImpl(
            @Value("${resend.api.key}") String resendApiKey,
            @Value("${resend.api.from}") String resendFrom,
            @Value("${admin.email}") String adminEmail,
            @Lazy EmailService self) {
        this.resendApiKey = resendApiKey;
        this.resendFrom = resendFrom;
        this.adminEmail = adminEmail;
        this.self = self;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void sendJobApplication(JobApplicationRequest request, MultipartFile cv) {
        // Leggiamo i bytes del CV o generiamo l'errore se impossibile prima che il
        // thread muoia
        byte[] cvBytes = null;
        String cvFileName = null;

        if (cv != null && !cv.isEmpty()) {
            try {
                cvBytes = cv.getBytes();
                cvFileName = cv.getOriginalFilename();
            } catch (IOException e) {
                log.error("Errore nella lettura del CV", e);
            }
        }

        // Delega al proxy Async
        self.sendEmailAsync(request, cvBytes, cvFileName, "application/pdf");
    }

    @Override
    @Async
    public void sendEmailAsync(JobApplicationRequest request, byte[] cvBytes, String cvFileName,
            String cvContentType) {
        try {
            String roleName = "PERSONAL_TRAINER".equals(request.getRole()) ? "Personal Trainer" : "Nutrizionista";
            String subject = "Nuova Candidatura — " + request.getFirstName() + " " + request.getLastName() + " — "
                    + roleName;
            String htmlBody = buildHtmlBody(request, roleName);

            // Costruiamo il payload JSON per l'API di Resend
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", resendFrom);
            payload.put("to", new String[] { adminEmail });
            payload.put("subject", subject);
            payload.put("html", htmlBody);

            // Alleghiamo il CV formattandolo in Base64 (Richiesto da Resend)
            if (cvBytes != null && cvBytes.length > 0) {
                List<Map<String, String>> attachments = new ArrayList<>();
                Map<String, String> attachment = new HashMap<>();
                attachment.put("filename", cvFileName != null ? cvFileName : "CV.pdf");
                attachment.put("content", Base64.getEncoder().encodeToString(cvBytes));
                attachments.add(attachment);
                payload.put("attachments", attachments);
            }

            // Headers della richiesta (API Key)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(resendApiKey);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(payload, headers);

            // Lancia la richiesta HTTP bloccante (che ora vive e sosta dentro questo thread
            // asincrono separato)
            ResponseEntity<String> response = restTemplate.postForEntity("https://api.resend.com/emails", httpEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Email di candidatura inviata con successo via Resend API da: {} {}", request.getFirstName(),
                        request.getLastName());
            } else {
                log.error("Fallimento imprevisto Resend API. Stato: {}, Risposta: {}", response.getStatusCode(),
                        response.getBody());
            }

        } catch (Exception e) {
            log.error("Errore di rete/imprevisto durante l'invio dell'email via Resend API", e);
        }
    }

    private String buildHtmlBody(JobApplicationRequest request, String roleName) {
        return "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #f8f9fa; border-radius: 12px; overflow: hidden;\">"
                + "<div style=\"background: linear-gradient(135deg, #1a3462, #112240); padding: 30px; text-align: center;\">"
                + "<h1 style=\"color: #c9a96e; margin: 0; font-size: 24px;\">&#127947; Nuova Candidatura</h1>"
                + "<p style=\"color: #b1c0d4; margin: 8px 0 0;\">Richiesta di collaborazione su Naval Gold</p>"
                + "</div>"
                + "<div style=\"padding: 30px;\">"
                + "<table style=\"width: 100%; border-collapse: collapse;\">"
                + "<tr><td style=\"padding: 12px 0; border-bottom: 1px solid #e9ecef; font-weight: bold; color: #495057; width: 140px;\">Nome</td>"
                + "<td style=\"padding: 12px 0; border-bottom: 1px solid #e9ecef; color: #212529;\">"
                + request.getFirstName() + "</td></tr>"
                + "<tr><td style=\"padding: 12px 0; border-bottom: 1px solid #e9ecef; font-weight: bold; color: #495057;\">Cognome</td>"
                + "<td style=\"padding: 12px 0; border-bottom: 1px solid #e9ecef; color: #212529;\">"
                + request.getLastName() + "</td></tr>"
                + "<tr><td style=\"padding: 12px 0; border-bottom: 1px solid #e9ecef; font-weight: bold; color: #495057;\">Email</td>"
                + "<td style=\"padding: 12px 0; border-bottom: 1px solid #e9ecef; color: #212529;\"><a href=\"mailto:"
                + request.getEmail() + "\" style=\"color: #1a3462;\">" + request.getEmail() + "</a></td></tr>"
                + "<tr><td style=\"padding: 12px 0; border-bottom: 1px solid #e9ecef; font-weight: bold; color: #495057;\">Ruolo richiesto</td>"
                + "<td style=\"padding: 12px 0; border-bottom: 1px solid #e9ecef; color: #212529;\"><span style=\"background: #c9a96e; color: #1a3462; padding: 4px 12px; border-radius: 20px; font-weight: bold; font-size: 13px;\">"
                + roleName + "</span></td></tr>"
                + "</table>"
                + "<div style=\"margin-top: 24px; padding: 20px; background: #fff; border-radius: 8px; border: 1px solid #e9ecef;\">"
                + "<h3 style=\"margin: 0 0 12px; color: #495057; font-size: 15px;\">&#128221; Messaggio Motivazionale</h3>"
                + "<p style=\"margin: 0; color: #212529; line-height: 1.6; white-space: pre-wrap;\">"
                + request.getMessage() + "</p>"
                + "</div></div>"
                + "<div style=\"background: #e9ecef; padding: 16px; text-align: center; font-size: 13px; color: #6c757d;\">"
                + "Email generata automaticamente da Naval Gold Platform"
                + "</div></div>";
    }
}
