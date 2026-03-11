package com.project.tesi.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configurazione di Swagger/OpenAPI per la documentazione interattiva delle API.
 *
 * Configura:
 * <ul>
 *   <li>Schema di autenticazione "Bearer Authentication" (JWT) per testare le API protette</li>
 *   <li>Server di produzione e sviluppo (entrambi su localhost:8080)</li>
 * </ul>
 *
 * L'interfaccia Swagger UI è accessibile all'indirizzo {@code /swagger-ui.html}.
 */
@Configuration
public class Swagger {

    /**
     * Crea il bean OpenAPI con lo schema di autenticazione JWT
     * e la lista dei server disponibili.
     *
     * @return la configurazione OpenAPI
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(
                        new SecurityRequirement().addList("Bearer Authentication")
                )
                .components(
                        new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme())
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Production Server"),
                        new Server().url("http://localhost:8080").description("Development Server")
                ));
    }

    /**
     * Crea lo schema di sicurezza per l'autenticazione Bearer JWT.
     *
     * @return lo schema SecurityScheme configurato per JWT
     */
    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

}