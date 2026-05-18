package com.project.tesi.config;

import com.project.tesi.service.DatabaseInitializerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Inizializza o resetta il database all'avvio.
 * È una scorciatoia comodissima in fase di sviluppo per avere sempre un set di dati pulito 
 * (utenti, piani, slot) senza dover ricreare tutto a mano ogni volta.
 */
@Configuration
public class DataInitializer {

    private final DatabaseInitializerService databaseInitializerService;

    public DataInitializer(DatabaseInitializerService databaseInitializerService) {
        this.databaseInitializerService = databaseInitializerService;
    }

    // Lancia il popolamento dei dati di test al bootstrap
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            databaseInitializerService.initialize();
        };
    }
}