package com.project.tesi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class TesiApplication {

    public static void main(String[] args) {
        // Forza l'utilizzo di IPv4 per risolvere problemi di timeout (es. verso
        // smtp.gmail.com su IPv6)
        System.setProperty("java.net.preferIPv4Stack", "true");

        // Inserimento automatico della variabile JWT_SECRET qualora non venga definita
        // manualmente dall'ambiente
        // per facilitare il run tramite VS Code/IDE.
        if (System.getenv("JWT_SECRET") == null && System.getProperty("JWT_SECRET") == null) {
            System.setProperty("JWT_SECRET", "QuestaEunaChiaveSegretaMoltoLungaPerIlMioProgettoTesi12345!");
        }

        SpringApplication.run(TesiApplication.class, args);
    }
}
