package com.project.tesi.service.impl;

import com.project.tesi.dto.request.JobApplicationRequest;
import com.project.tesi.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final String adminEmail;

    public EmailServiceImpl(JavaMailSender mailSender,
            @Value("${spring.mail.username}") String adminEmail) {
        this.mailSender = mailSender;
        this.adminEmail = adminEmail;
    }

    @Override
    public void sendJobApplication(JobApplicationRequest request, MultipartFile cv) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(adminEmail);
            helper.setFrom(adminEmail);
            helper.setReplyTo(request.getEmail());

            String roleName = "PERSONAL_TRAINER".equals(request.getRole()) ? "Personal Trainer" : "Nutrizionista";
            helper.setSubject(
                    "Nuova Candidatura — " + request.getFirstName() + " " + request.getLastName() + " — " + roleName);

            String htmlBody = buildHtmlBody(request, roleName);
            helper.setText(htmlBody, true);

            // Allega il CV se presente
            if (cv != null && !cv.isEmpty()) {
                helper.addAttachment(cv.getOriginalFilename(), cv);
            }

            mailSender.send(mimeMessage);
            log.info("Email di candidatura inviata con successo da: {} {}", request.getFirstName(),
                    request.getLastName());

        } catch (MessagingException e) {
            log.error("Errore durante l'invio dell'email di candidatura", e);
            throw new RuntimeException("Impossibile inviare l'email di candidatura. Riprova più tardi.", e);
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
