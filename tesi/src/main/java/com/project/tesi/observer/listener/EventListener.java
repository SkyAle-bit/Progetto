package com.project.tesi.observer.listener;

/**
 * Interfaccia base per il Design Pattern Observer.
 * Qualsiasi listener che vuole reagire a un evento del sistema deve implementare questa interfaccia.
 * 
 * @param <T> Il tipo di dato passato assieme all'evento.
 */
public interface EventListener<T> {
    void update(T data);
}
