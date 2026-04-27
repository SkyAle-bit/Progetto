package com.project.tesi.observer.manager;

import com.project.tesi.enums.EventType;
import com.project.tesi.observer.listener.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Gestore degli eventi (Subject/Broker) per il Design Pattern Observer.
 * Permette la registrazione e la notifica dei listener per specifici eventi.
 */
@Component
public class EventManager {

    private final Map<EventType, List<EventListener<?>>> listeners;

    public EventManager() {
        this.listeners = new EnumMap<>(EventType.class);
        for (EventType type : EventType.values()) {
            this.listeners.put(type, new ArrayList<>());
        }
    }

    /**
     * Iscrive un listener a uno specifico tipo di evento.
     */
    public <T> void subscribe(EventType eventType, EventListener<T> listener) {
        listeners.get(eventType).add(listener);
    }

    /**
     * Disiscrive un listener da uno specifico tipo di evento.
     */
    public <T> void unsubscribe(EventType eventType, EventListener<T> listener) {
        listeners.get(eventType).remove(listener);
    }

    /**
     * Notifica tutti i listener iscritti a un determinato evento.
     */
    @SuppressWarnings("unchecked")
    public <T> void notifyListeners(EventType eventType, T data) {
        List<EventListener<?>> users = listeners.get(eventType);
        for (EventListener<?> listener : users) {
            ((EventListener<T>) listener).update(data);
        }
    }
}
