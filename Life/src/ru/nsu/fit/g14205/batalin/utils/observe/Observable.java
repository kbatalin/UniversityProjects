package ru.nsu.fit.g14205.batalin.utils.observe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kir55rus on 01.03.17.
 */
public class Observable implements IObservable {
    private Map<IEvent, Map<Integer, IObserverHandler>> observers;

    public Observable() {
        clear();
    }

    private void clear() {
        observers = new HashMap<>();
    }

    @Override
    public int addObserver(IEvent event, IObserverHandler handler) {
        Map<Integer, IObserverHandler> list = observers.computeIfAbsent(event, k -> new HashMap<>());
        list.put(handler.hashCode(), handler);

        return handler.hashCode();
    }

    @Override
    public void deleteObserver(IEvent event, int id) {
        Map<Integer, IObserverHandler> handlers = observers.get(event);
        if (handlers == null) {
            return;
        }

        handlers.remove(id);
    }

    @Override
    public void notifyObservers(IEvent event) {
        Map<Integer, IObserverHandler> handlers = observers.get(event);
        if (handlers == null) {
            return;
        }

        for (IObserverHandler handler : handlers.values()) {
            handler.perform();
        }
    }

    @Override
    public void deleteObservers() {
        clear();
    }
}
