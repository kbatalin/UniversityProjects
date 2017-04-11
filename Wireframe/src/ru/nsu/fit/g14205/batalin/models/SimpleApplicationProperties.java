package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kir55rus on 12.04.17.
 */
public class SimpleApplicationProperties extends ObservableBase implements ApplicationProperties {
    private double controlPointRadius;
    private ArrayList<LineProperties> lineProperties;

    public SimpleApplicationProperties() {
        controlPointRadius = .2;
        lineProperties = new ArrayList<>();
    }

    @Override
    public double getControlPointRadius() {
        return controlPointRadius;
    }

    @Override
    public List<LineProperties> getLineProperties() {
        return lineProperties;
    }

    @Override
    public void addLineProperties(LineProperties properties) {
        lineProperties.add(properties);
        notifyObservers(Event.LINE_PROPERTIES_ADDED);
    }

    @Override
    public void delLineProperties(int index) {
        lineProperties.remove(index);
        notifyObservers(Event.LINE_PROPERTIES_REMOVED);
    }
}
