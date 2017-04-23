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
    private CameraProperties cameraProperties;
    private ViewPyramidProperties viewPyramidProperties;

    public SimpleApplicationProperties() {
        controlPointRadius = .3;
        lineProperties = new ArrayList<>();
        area = new Area(0, 0, 1, 2 * Math.PI);
        cameraProperties = new Camera(new Point3D(-10, 0, 0), new Point3D(10, 0, 0), new Point3D(0, 1, 0));
        viewPyramidProperties = new ViewPyramid(5, 15, 1, 1);
    }

    @Override
    public ApplicationProperties clone() throws CloneNotSupportedException {
        SimpleApplicationProperties applicationProperties = (SimpleApplicationProperties) super.clone();
        applicationProperties.controlPointRadius = controlPointRadius;
        applicationProperties.cameraProperties = cameraProperties.clone();
        applicationProperties.area = area.clone();
        applicationProperties.viewPyramidProperties = viewPyramidProperties.clone();
        applicationProperties.lineProperties = new ArrayList<>();
        for (LineProperties line : lineProperties) {
            applicationProperties.lineProperties.add(line.clone());
        }
        return applicationProperties;
    }

    @Override
    public void apply(ApplicationProperties applicationProperties) {
        controlPointRadius = applicationProperties.getControlPointRadius();
        lineProperties = new ArrayList<>();
        lineProperties.addAll(applicationProperties.getLineProperties());
        area = applicationProperties.getArea();
        cameraProperties = applicationProperties.getCameraProperties();
        viewPyramidProperties = applicationProperties.getViewPyramidProperties();
    }

    @Override
    public ViewPyramidProperties getViewPyramidProperties() {
        return viewPyramidProperties;
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

    @Override
    public CameraProperties getCameraProperties() {
        return cameraProperties;
    }
}
