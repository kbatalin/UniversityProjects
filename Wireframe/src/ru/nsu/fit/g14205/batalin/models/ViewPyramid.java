package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class ViewPyramid extends ObservableBase implements ViewPyramidProperties {
    private double backPlaneDistance;
    private double frontPlaneDistance;
    private double frontPlaneWidth;
    private double frontPlaneHeight;

    public ViewPyramid() {
    }

    public ViewPyramid(double backPlaneDistance, double frontPlaneDistance, double frontPlaneWidth, double frontPlaneHeight) {
        this.backPlaneDistance = backPlaneDistance;
        this.frontPlaneDistance = frontPlaneDistance;
        this.frontPlaneWidth = frontPlaneWidth;
        this.frontPlaneHeight = frontPlaneHeight;
    }

    @Override
    public double getBackPlaneDistance() {
        return backPlaneDistance;
    }

    @Override
    public void setBackPlaneDistance(double backPlaneDistance) {
        this.backPlaneDistance = backPlaneDistance;
        notifyObservers(Event.BACK_PLANE_DISTANCE_CHANGED);
    }

    @Override
    public double getFrontPlaneDistance() {
        return frontPlaneDistance;
    }

    @Override
    public void setFrontPlaneDistance(double frontPlaneDistance) {
        this.frontPlaneDistance = frontPlaneDistance;
        notifyObservers(Event.FRONT_PLANE_DISTANCE_CHANGED);
    }

    @Override
    public double getFrontPlaneWidth() {
        return frontPlaneWidth;
    }

    @Override
    public void setFrontPlaneWidth(double frontPlaneWidth) {
        this.frontPlaneWidth = frontPlaneWidth;
        notifyObservers(Event.FRONT_PLANE_SIZE_CHANGED);
    }

    @Override
    public double getFrontPlaneHeight() {
        return frontPlaneHeight;
    }

    @Override
    public void setFrontPlaneHeight(double frontPlaneHeight) {
        this.frontPlaneHeight = frontPlaneHeight;
        notifyObservers(Event.FRONT_PLANE_SIZE_CHANGED);
    }

    @Override
    public ViewPyramidProperties clone() throws CloneNotSupportedException {
        ViewPyramid viewPyramid = (ViewPyramid) super.clone();
        viewPyramid.backPlaneDistance = backPlaneDistance;
        viewPyramid.frontPlaneDistance = frontPlaneDistance;
        viewPyramid.frontPlaneWidth = frontPlaneWidth;
        viewPyramid.frontPlaneHeight = frontPlaneHeight;
        return viewPyramid;
    }
}
