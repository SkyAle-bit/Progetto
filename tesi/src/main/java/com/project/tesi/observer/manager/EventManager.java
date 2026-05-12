package com.project.tesi.observer.manager;

import com.project.tesi.enums.EventType;
import com.project.tesi.observer.listener.Observer;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class EventManager implements Subject {

    private final Map<EventType, List<Observer<?>>> listeners;

    public EventManager() {
        this.listeners = new EnumMap<>(EventType.class);
        for (EventType type : EventType.values()) {
            // CopyOnWriteArrayList è thread-safe: essenziale in un contesto web
            this.listeners.put(type, new CopyOnWriteArrayList<>());
        }
    }

    public <T> void subscribe(EventType eventType, Observer<T> listener) {
        listeners.get(eventType).add(listener);
    }

    public <T> void unsubscribe(EventType eventType, Observer<T> listener) {
        listeners.get(eventType).remove(listener);
    }

    /**
     * Spara l'evento a tutti i listener registrati.
     *
     * Nota su @SuppressWarnings("unchecked"): 
     * In Java c'è la "type erasure", quindi a runtime perdiamo il tipo esatto dei Generics. 
     * Avendo una mappa eterogenea di Observer, il compilatore si lamenta del cast. 
     * Lo sopprimiamo perché sappiamo (grazie al metodo subscribe) che la mappa è consistente:
     * chi si iscrive a BOOKING_CREATED riceve per forza un oggetto Booking. È un compromesso
     * accettabile per avere un event bus centralizzato pulito.
     */
    @SuppressWarnings("unchecked")
    public <T> void notifyListeners(EventType eventType, T data) {
        List<Observer<?>> users = listeners.get(eventType);
        for (Observer<?> listener : users) {
            ((Observer<T>) listener).update(data);
        }
    }
}
