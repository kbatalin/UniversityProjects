package pro.batalin.models.observe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kir55rus on 01.03.17.
 */
public class ObservableBase implements Observable {
    private Map<ObserveEvent, Map<Integer, ObserverHandler>> observers;

    public ObservableBase() {
        clear();
    }

    private void clear() {
        observers = new ConcurrentHashMap<>();
    }

    @Override
    public int addObserver(ObserveEvent event, ObserverHandler handler) {
        Map<Integer, ObserverHandler> list = observers.computeIfAbsent(event, k -> new ConcurrentHashMap<>());
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
        notifyObservers(event, null);
    }

    @Override
    public void notifyObservers(ObserveEvent event, Object args) {
        Map<Integer, ObserverHandler> handlers = observers.get(event);
        if (handlers == null) {
            return;
        }

        for (ObserverHandler handler : handlers.values()) {
            handler.perform(args);
        }
    }

    @Override
    public void deleteObservers() {
        clear();
    }
}
