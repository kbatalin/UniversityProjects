package ru.nsu.fit.g14205.batalin.models;

import java.util.Observer;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface IObservable {
    void addObserver(Observer var1);
    void deleteObserver(Observer var1);
    void notifyObservers();
    void notifyObservers(Object var1);
    void deleteObservers();
    boolean hasChanged();
    int countObservers();
}
