package ru.nsu.fit.g14205.batalin.utils.observe;

import java.util.function.Supplier;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface IObservable {
    int addObserver(EventBase event, Supplier<Void> handler);
    void deleteObserver(EventBase event, int id);
    void notifyObservers(EventBase event);
    void deleteObservers();
}
