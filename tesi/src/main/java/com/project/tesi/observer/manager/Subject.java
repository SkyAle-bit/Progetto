package com.project.tesi.observer.manager;

import com.project.tesi.enums.EventType;
import com.project.tesi.observer.listener.Observer;

public interface Subject {
    <T> void subscribe(EventType eventType, Observer<T> listener);
    <T> void unsubscribe(EventType eventType, Observer<T> listener);
    <T> void notifyListeners(EventType eventType, T data);
}
