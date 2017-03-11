package ru.nsu.fit.g14205.batalin.utils.observe;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface IObservable {
    int addObserver(IEvent event, IObserverHandler handler);
    void deleteObserver(IEvent event, int id);
    void notifyObservers(IEvent event);
    void deleteObservers();
}
