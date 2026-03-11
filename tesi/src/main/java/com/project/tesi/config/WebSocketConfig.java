package com.project.tesi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configurazione WebSocket con protocollo STOMP per la chat in tempo reale.
 *
 * Definisce:
 * <ul>
 *   <li>Il message broker in-memory con prefissi {@code /topic} (broadcast) e {@code /queue} (utente singolo)</li>
 *   <li>Il prefisso {@code /app} per i messaggi inviati dal client al server</li>
 *   <li>Il prefisso {@code /user} per i messaggi destinati a un utente specifico (notifiche)</li>
 *   <li>Due endpoint STOMP: uno con SockJS fallback ({@code /ws}) e uno nativo ({@code /ws/websocket})</li>
 * </ul>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura il message broker STOMP.
     * <ul>
     *   <li>{@code /topic} — canali di broadcast (es. stanze chat)</li>
     *   <li>{@code /queue} — code personali per utente (es. notifiche)</li>
     *   <li>{@code /app} — prefisso per i messaggi inviati dal client (mappati su {@code @MessageMapping})</li>
     *   <li>{@code /user} — prefisso per l'instradamento ai singoli utenti</li>
     * </ul>
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Registra gli endpoint STOMP a cui i client si connettono.
     * <ul>
     *   <li>{@code /ws} — con SockJS fallback per browser legacy</li>
     *   <li>{@code /ws/websocket} — WebSocket nativo per client moderni (es. @stomp/stompjs)</li>
     * </ul>
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint con SockJS fallback (per client legacy)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Endpoint WebSocket nativo (per client moderni come @stomp/stompjs)
        registry.addEndpoint("/ws/websocket")
                .setAllowedOriginPatterns("*");
    }
}
