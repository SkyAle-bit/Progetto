package com.project.tesi.config;

import com.project.tesi.service.DatabaseInitializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione per l'inizializzazione automatica del database.
 * Viene eseguita una sola volta all'avvio dell'applicazione tramite
 * un {@link CommandLineRunner} che delega al {@link DatabaseInitializerService}
 * il popolamento dei dati di test (utenti, piani, slot, ecc.).
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final DatabaseInitializerService databaseInitializerService;

    /**
     * Bean eseguito automaticamente all'avvio di Spring Boot.
     * Popola il database con i dati iniziali di test.
     *
     * @return il CommandLineRunner che invoca l'inizializzazione
     */
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            databaseInitializerService.initialize();
        };
    }
}