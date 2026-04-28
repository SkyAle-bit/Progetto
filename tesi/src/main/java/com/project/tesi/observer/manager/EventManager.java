package com.project.tesi.observer.manager;

import com.project.tesi.enums.EventType;
import com.project.tesi.observer.listener.EventListener;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EventManager {

    private static final Logger log = LoggerFactory.getLogger(EventManager.class);

    private final Map<EventType, List<EventListener<?>>> listeners;

    public EventManager() {
        this.listeners = new EnumMap<>(EventType.class);
        for (EventType type : EventType.values()) {
            // CopyOnWriteArrayList è thread-safe: essenziale in un contesto web
            this.listeners.put(type, new CopyOnWriteArrayList<>());
        }
    }

    public <T> void subscribe(EventType eventType, EventListener<T> listener) {
        listeners.get(eventType).add(listener);
    }

    public <T> void unsubscribe(EventType eventType, EventListener<T> listener) {
        listeners.get(eventType).remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void notifyListeners(EventType eventType, T data) {
        List<EventListener<?>> users = listeners.get(eventType);
        for (EventListener<?> listener : users) {
            try {
                ((EventListener<T>) listener).update(data);
            } catch (ClassCastException e) {
                log.error("Errore di Type-Casting durante l'update del listener per l'evento {}: {}", eventType, e.getMessage());
            }
        }
    }
}
