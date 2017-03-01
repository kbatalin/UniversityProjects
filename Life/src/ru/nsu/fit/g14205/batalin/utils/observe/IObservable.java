package ru.nsu.fit.g14205.batalin.utils.observe;

import java.util.function.Supplier;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface IObservable {
    int addObserver(IEvent event, Supplier<Void> handler);
    void deleteObserver(IEvent event, int id);
    void notifyObservers(IEvent event);
    void deleteObservers();
}
