package com.project.tesi.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitari per {@link WebSocketEventListener}.
 * Usa ReflectionTestUtils per accedere alle mappe interne e simulare
 * il flusso completo connect → join → isUserInRoom → leave → disconnect.
 */
class WebSocketEventListenerTest {

    private WebSocketEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new WebSocketEventListener();
    }


    @Test @DisplayName("joinRoom — sessione non registrata → non lancia eccezione")
    void joinRoom_unknownSession() {
        listener.joinRoom("unknown-session", "room-1-2");
        // sessionRooms non contiene "unknown-session" → rooms == null → nessun effetto
    }

    @Test @DisplayName("leaveRoom — sessione non registrata → non lancia eccezione")
    void leaveRoom_unknownSession() {
        listener.leaveRoom("unknown-session", "room-1-2");
    }


    @Test @DisplayName("isUserInRoom — utente non connesso → false")
    void isUserInRoom_noUser() {
        assertThat(listener.isUserInRoom(999L, "room-1-2")).isFalse();
    }


    @SuppressWarnings("unchecked")
    @Test @DisplayName("flusso completo: simulazione connect → joinRoom → isUserInRoom → leaveRoom → disconnect")
    void fullFlow_viaReflection() {
        // Simuliamo una connessione settando direttamente le mappe interne
        Map<Long, Set<String>> userSessions = (Map<Long, Set<String>>)
                ReflectionTestUtils.getField(listener, "userSessions");
        Map<String, Long> sessionUser = (Map<String, Long>)
                ReflectionTestUtils.getField(listener, "sessionUser");
        Map<String, Set<String>> sessionRooms = (Map<String, Set<String>>)
                ReflectionTestUtils.getField(listener, "sessionRooms");

        // Simuliamo handleConnect: utente 1, sessione "sess-A"
        String sessionId = "sess-A";
        Long userId = 1L;
        sessionUser.put(sessionId, userId);
        userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        sessionRooms.put(sessionId, ConcurrentHashMap.newKeySet());

        // L'utente NON è ancora nella stanza
        assertThat(listener.isUserInRoom(1L, "room-1-2")).isFalse();

        // joinRoom: l'utente entra nella stanza
        listener.joinRoom(sessionId, "room-1-2");

        // Ora l'utente È nella stanza
        assertThat(listener.isUserInRoom(1L, "room-1-2")).isTrue();

        // Ma non in un'altra stanza
        assertThat(listener.isUserInRoom(1L, "room-3-4")).isFalse();

        // leaveRoom: l'utente esce dalla stanza
        listener.leaveRoom(sessionId, "room-1-2");

        // L'utente non è più nella stanza
        assertThat(listener.isUserInRoom(1L, "room-1-2")).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test @DisplayName("isUserInRoom — utente con sessione ma sessionRooms null per quella sessione")
    void isUserInRoom_sessionRoomsNull() {
        Map<Long, Set<String>> userSessions = (Map<Long, Set<String>>)
                ReflectionTestUtils.getField(listener, "userSessions");

        // L'utente ha una sessione ma sessionRooms non la contiene
        userSessions.computeIfAbsent(1L, k -> ConcurrentHashMap.newKeySet()).add("orphan-session");

        // rooms == null per "orphan-session" → false
        assertThat(listener.isUserInRoom(1L, "room-1-2")).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test @DisplayName("più sessioni per lo stesso utente — una nella stanza, una no")
    void isUserInRoom_multipleSessions() {
        Map<Long, Set<String>> userSessions = (Map<Long, Set<String>>)
                ReflectionTestUtils.getField(listener, "userSessions");
        Map<String, Set<String>> sessionRooms = (Map<String, Set<String>>)
                ReflectionTestUtils.getField(listener, "sessionRooms");

        // Utente con 2 sessioni
        userSessions.computeIfAbsent(1L, k -> ConcurrentHashMap.newKeySet()).add("sess-A");
        userSessions.get(1L).add("sess-B");
        sessionRooms.put("sess-A", ConcurrentHashMap.newKeySet());
        sessionRooms.put("sess-B", ConcurrentHashMap.newKeySet());

        // Solo sess-A è nella stanza
        listener.joinRoom("sess-A", "room-1-2");

        assertThat(listener.isUserInRoom(1L, "room-1-2")).isTrue();
    }
}
