package ru.nsu.fit.g14205.batalin.utils.observe;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface Observable {
    int addObserver(ObserveEvent event, ObserverHandler handler);
    void deleteObserver(ObserveEvent event, int id);
    void notifyObservers(ObserveEvent event);
    void deleteObservers();
}
