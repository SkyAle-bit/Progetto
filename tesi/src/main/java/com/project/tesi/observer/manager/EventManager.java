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
     * Notifica tutti i listener registrati per un determinato tipo di evento.
     *
     * <p><b>Nota tecnica sull'uso di {@code @SuppressWarnings("unchecked")}:</b><br>
     * L'uso del cast esplicito a {@code Observer<T>} è reso necessario dalla 
     * <i>type erasure</i> dei Generics in Java. Poiché i listener sono memorizzati 
     * in una mappa eterogenea ({@code Map<EventType, List<Observer<?>>>}), 
     * l'informazione sul tipo specifico viene persa a runtime.</p>
     *
     * <p>La scelta di sopprimere il warning è giustificata dal design del sistema:
     * il metodo {@link #subscribe(EventType, Observer)} garantisce per contratto
     * che il listener registrato sia compatibile con l'oggetto {@code data} inviato
     * per quel determinato {@code EventType}. Questa è una soluzione pragmatica e 
     * standard per implementare un Event Manager centralizzato e type-safe a livello 
     * di interfaccia pubblica, pur accettando un limite tecnico del compilatore 
     * nel layer interno.</p>
     *
     * @param eventType il tipo di evento emesso
     * @param data      i dati associati all'evento (es. entità Booking)
     * @param <T>       il tipo generico dei dati
     */
    @SuppressWarnings("unchecked")
    public <T> void notifyListeners(EventType eventType, T data) {
        List<Observer<?>> users = listeners.get(eventType);
        for (Observer<?> listener : users) {
            ((Observer<T>) listener).update(data);
        }
    }
}
