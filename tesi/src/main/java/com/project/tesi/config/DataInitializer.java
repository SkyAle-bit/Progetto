package com.project.tesi.config;

import com.project.tesi.service.DatabaseInitializerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final DatabaseInitializerService databaseInitializerService;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            databaseInitializerService.initialize();
        };
    }
}