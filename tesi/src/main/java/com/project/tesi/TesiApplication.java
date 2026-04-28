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
        // Forza l'utilizzo di IPv4 per risolvere problemi di timeout (es. verso smtp.gmail.com su IPv6)
        System.setProperty("java.net.preferIPv4Stack", "true");



        SpringApplication.run(TesiApplication.class, args);
    }
}
