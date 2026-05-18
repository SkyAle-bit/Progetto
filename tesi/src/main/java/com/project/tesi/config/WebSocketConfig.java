package com.project.tesi.config;

import com.project.tesi.security.WebSocketChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configurazione del broker WebSocket (STOMP).
 * Smistiamo il traffico su due canali: /topic per i broadcast e /queue per i messaggi privati.
 * I client si collegano a /ws o /ws/websocket per bypassare eventuali firewall.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketChannelInterceptor webSocketChannelInterceptor;

    public WebSocketConfig(WebSocketChannelInterceptor webSocketChannelInterceptor) {
        this.webSocketChannelInterceptor = webSocketChannelInterceptor;
    }

    // Configura il broker in-memory e i prefissi per l'instradamento dei messaggi
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketChannelInterceptor);
    }

    // Espone l'endpoint di connessione, con o senza fallback SockJS
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
