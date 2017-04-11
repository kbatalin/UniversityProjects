package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

import java.util.List;

/**
 * Created by kir55rus on 12.04.17.
 */
public interface ApplicationProperties extends Observable {
    double getControlPointRadius();

    List<LineProperties> getLineProperties();
    void addLineProperties(LineProperties lineProperties);
    void delLineProperties(int index);

    enum Event implements ObserveEvent {
        LINE_PROPERTIES_ADDED,
        LINE_PROPERTIES_REMOVED,
    }
}
