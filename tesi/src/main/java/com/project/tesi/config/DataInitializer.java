package com.project.tesi.config;

import com.project.tesi.service.DatabaseInitializerService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.project.tesi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inizializza o resetta il database all'avvio.
 * È una scorciatoia comodissima in fase di sviluppo per avere sempre un set di dati pulito 
 * (utenti, piani, slot) senza dover ricreare tutto a mano ogni volta.
 */



/**
 * Inizializza o resetta il database all'avvio.
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final DatabaseInitializerService databaseInitializerService;
    private final UserRepository userRepository;

    public DataInitializer(DatabaseInitializerService databaseInitializerService, UserRepository userRepository) {
        this.databaseInitializerService = databaseInitializerService;
        this.userRepository = userRepository;
    }

    // Lancia il popolamento dei dati di test al bootstrap
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Database vuoto! Inizio il popolamento dei dati di test...");
                try {
                    databaseInitializerService.initialize();
                    log.info("Dati di test inseriti con successo!");
                } catch (Exception e) {
                    log.error("ERRORE CRITICO DURANTE L'INIZIALIZZAZIONE DEL DATABASE: ", e);
                }
            } else {
                log.info("Database già popolato. Salto l'inizializzazione.");
            }
        };
    }
}