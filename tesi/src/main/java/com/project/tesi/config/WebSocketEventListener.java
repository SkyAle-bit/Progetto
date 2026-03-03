package com.project.tesi.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    private final Map<Long, Set<String>> userSessions = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionUser = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sessionRooms = new ConcurrentHashMap<>();

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

    public void joinRoom(String sessionId, String roomId) {
        Set<String> rooms = sessionRooms.get(sessionId);
        if (rooms != null) rooms.add(roomId);
    }

    public void leaveRoom(String sessionId, String roomId) {
        Set<String> rooms = sessionRooms.get(sessionId);
        if (rooms != null) rooms.remove(roomId);
    }

    public boolean isUserInRoom(Long userId, String roomId) {
        Set<String> sessions = userSessions.get(userId);
        if (sessions == null) return false;
        return sessions.stream().anyMatch(sid -> {
            Set<String> rooms = sessionRooms.get(sid);
            return rooms != null && rooms.contains(roomId);
        });
    }
}
