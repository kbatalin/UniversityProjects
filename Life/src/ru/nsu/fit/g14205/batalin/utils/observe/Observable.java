package ru.nsu.fit.g14205.batalin.utils.observe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by kir55rus on 01.03.17.
 */
public class Observable {
    private Map<EventBase, Map<Integer, Supplier<Void>>> observers;

    public Observable() {
        clear();
    }

    private void clear() {
        observers = new HashMap<>();
    }

    public int addObserver(EventBase event, Supplier<Void> handler) {
        Map<Integer, Supplier<Void>> list = observers.computeIfAbsent(event, k -> {
            Map<Integer, Supplier<Void>> handlers = new HashMap<>();
            handlers.put(handler.hashCode(), handler);
            return handlers;
        });

        return handler.hashCode();
    }

    public void deleteObserver(EventBase event, int id) {
        Map<Integer, Supplier<Void>> handlers = observers.get(event);
        if (handlers == null) {
            return;
        }

        handlers.remove(id);
    }

    public void notifyObservers(EventBase event) {
        Map<Integer, Supplier<Void>> handlers = observers.get(event);
        if (handlers == null) {
            return;
        }

        for (Supplier<Void> handler : handlers.values()) {
            handler.get();
        }
    }

    public void deleteObservers() {
        clear();
    }
}
