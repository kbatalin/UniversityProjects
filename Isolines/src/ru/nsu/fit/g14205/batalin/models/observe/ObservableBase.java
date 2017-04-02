package ru.nsu.fit.g14205.batalin.models.observe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kir55rus on 01.03.17.
 */
public class ObservableBase implements Observable {
    private Map<ObserveEvent, Map<Integer, ObserverHandler>> observers;

    public ObservableBase() {
        clear();
    }

    private void clear() {
        observers = new HashMap<>();
    }

    @Override
    public int addObserver(ObserveEvent event, ObserverHandler handler) {
        Map<Integer, ObserverHandler> list = observers.computeIfAbsent(event, k -> new HashMap<>());
        list.put(handler.hashCode(), handler);

        return handler.hashCode();
    }

    @Override
    public void deleteObserver(ObserveEvent event, int id) {
        Map<Integer, ObserverHandler> handlers = observers.get(event);
        if (handlers == null) {
            return;
        }

        handlers.remove(id);
    }

    @Override
    public void notifyObservers(ObserveEvent event) {
        Map<Integer, ObserverHandler> handlers = observers.get(event);
        if (handlers == null) {
            return;
        }

        for (ObserverHandler handler : handlers.values()) {
            handler.perform();
        }
    }

    @Override
    public void deleteObservers() {
        clear();
    }
}
