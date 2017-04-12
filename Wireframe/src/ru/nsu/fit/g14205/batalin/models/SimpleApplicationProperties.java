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
    private Area area;

    public SimpleApplicationProperties() {
        controlPointRadius = .3;
        lineProperties = new ArrayList<>();
        area = new Area(0, 0, 1, 2 * Math.PI);
    }

    @Override
    public ApplicationProperties clone() throws CloneNotSupportedException {
        SimpleApplicationProperties applicationProperties = (SimpleApplicationProperties) super.clone();
        applicationProperties.controlPointRadius = controlPointRadius;
        applicationProperties.area = area.clone();
        applicationProperties.lineProperties = new ArrayList<>();
        for (LineProperties line : lineProperties) {
            applicationProperties.lineProperties.add(line.clone());
        }
        return applicationProperties;
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public void setArea(Area area) {
        this.area = area;
        notifyObservers(Event.AREA_CHANGED);
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
    public int getLinePropertiesCount() {
        return lineProperties.size();
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
