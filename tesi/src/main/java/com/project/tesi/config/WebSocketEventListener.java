package com.project.tesi.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Listener per gli eventi del ciclo di vita delle connessioni WebSocket.
 *
 * Gestisce tre strutture dati thread-safe (ConcurrentHashMap) per tracciare:
 * <ul>
 *   <li>{@code userSessions} — mappa userId → set di sessionId (un utente può avere più tab aperte)</li>
 *   <li>{@code sessionUser} — mappa inversa sessionId → userId (per il cleanup alla disconnessione)</li>
 *   <li>{@code sessionRooms} — mappa sessionId → set di roomId (stanze chat in cui la sessione è entrata)</li>
 * </ul>
 *
 * Queste informazioni servono a determinare se un utente è attivamente
 * presente in una stanza chat, per decidere se inviare notifiche push.
 */
@Component
public class WebSocketEventListener {

    /** Mappa userId → insieme delle sessioni WebSocket attive dell'utente. */
    private final Map<Long, Set<String>> userSessions = new ConcurrentHashMap<>();

    /** Mappa inversa sessionId → userId per il cleanup alla disconnessione. */
    private final Map<String, Long> sessionUser = new ConcurrentHashMap<>();

    /** Mappa sessionId → insieme delle stanze chat in cui la sessione è attualmente presente. */
    private final Map<String, Set<String>> sessionRooms = new ConcurrentHashMap<>();

    /**
     * Gestisce l'evento di connessione WebSocket STOMP.
     * Estrae l'header personalizzato {@code userId} e registra la sessione
     * nelle mappe di tracciamento.
     *
     * @param event evento di connessione STOMP
     */
    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        String userIdHeader = accessor.getFirstNativeHeader("userId");
        if (sessionId != null && userIdHeader != null) {
            Long userId = Long.parseLong(userIdHeader);
            sessionUser.put(sessionId, userId);
            userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
            sessionRooms.put(sessionId, ConcurrentHashMap.newKeySet());
        }
    }

    /**
     * Gestisce l'evento di disconnessione WebSocket.
     * Rimuove la sessione da tutte le mappe di tracciamento.
     *
     * @param event evento di disconnessione STOMP
     */
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        if (sessionId == null) return;
        Long userId = sessionUser.remove(sessionId);
        if (userId != null) {
            Set<String> sessions = userSessions.get(userId);
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) userSessions.remove(userId);
            }
        }
        sessionRooms.remove(sessionId);
    }

    /**
     * Registra l'ingresso di una sessione in una stanza chat.
     *
     * @param sessionId ID della sessione WebSocket
     * @param roomId    ID della stanza chat
     */
    public void joinRoom(String sessionId, String roomId) {
        Set<String> rooms = sessionRooms.get(sessionId);
        if (rooms != null) rooms.add(roomId);
    }

    /**
     * Registra l'uscita di una sessione da una stanza chat.
     *
     * @param sessionId ID della sessione WebSocket
     * @param roomId    ID della stanza chat
     */
    public void leaveRoom(String sessionId, String roomId) {
        Set<String> rooms = sessionRooms.get(sessionId);
        if (rooms != null) rooms.remove(roomId);
    }

    /**
     * Verifica se un utente è attualmente presente in una stanza chat.
     * Controlla tutte le sessioni attive dell'utente.
     *
     * @param userId ID dell'utente
     * @param roomId ID della stanza chat
     * @return {@code true} se almeno una sessione dell'utente è nella stanza
     */
    public boolean isUserInRoom(Long userId, String roomId) {
        Set<String> sessions = userSessions.get(userId);
        if (sessions == null) return false;
        return sessions.stream().anyMatch(sid -> {
            Set<String> rooms = sessionRooms.get(sid);
            return rooms != null && rooms.contains(roomId);
        });
    }
}
