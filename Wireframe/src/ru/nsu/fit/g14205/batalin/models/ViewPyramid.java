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
    private Matrix projectionMatrix;

    public ViewPyramid() {
    }

    public ViewPyramid(double backPlaneDistance, double frontPlaneDistance, double frontPlaneWidth, double frontPlaneHeight) {
        this.backPlaneDistance = backPlaneDistance;
        this.frontPlaneDistance = frontPlaneDistance;
        this.frontPlaneWidth = frontPlaneWidth;
        this.frontPlaneHeight = frontPlaneHeight;
        updMatrix();
    }

    private void updMatrix() {
        double zf = -getFrontPlaneDistance();
        double zb = -getBackPlaneDistance();
        double sw = getFrontPlaneWidth();
        double sh = getFrontPlaneHeight();
        projectionMatrix = new Matrix(4, 4, new double[]{
                2 * zf / sw, 0, 0, 0,
                0, 2 * zf / sh, 0, 0,
                0, 0, zf / (zb - zf), -zf * zb / (zb - zf),
                0, 0, 1, 0
        });
    }

    @Override
    public Matrix getProjectionMatrix() {
        return projectionMatrix;
    }

    @Override
    public double getBackPlaneDistance() {
        return backPlaneDistance;
    }

    @Override
    public void setBackPlaneDistance(double backPlaneDistance) {
        this.backPlaneDistance = backPlaneDistance;
        updMatrix();
        notifyObservers(Event.BACK_PLANE_DISTANCE_CHANGED);
    }

    @Override
    public double getFrontPlaneDistance() {
        return frontPlaneDistance;
    }

    @Override
    public void setFrontPlaneDistance(double frontPlaneDistance) {
        this.frontPlaneDistance = frontPlaneDistance;
        updMatrix();
        notifyObservers(Event.FRONT_PLANE_DISTANCE_CHANGED);
    }

    @Override
    public double getFrontPlaneWidth() {
        return frontPlaneWidth;
    }

    @Override
    public void setFrontPlaneWidth(double frontPlaneWidth) {
        this.frontPlaneWidth = frontPlaneWidth;
        updMatrix();
        notifyObservers(Event.FRONT_PLANE_SIZE_CHANGED);
    }

    @Override
    public double getFrontPlaneHeight() {
        return frontPlaneHeight;
    }

    @Override
    public void setFrontPlaneHeight(double frontPlaneHeight) {
        this.frontPlaneHeight = frontPlaneHeight;
        updMatrix();
        notifyObservers(Event.FRONT_PLANE_SIZE_CHANGED);
    }

    @Override
    public ViewPyramidProperties clone() throws CloneNotSupportedException {
        ViewPyramid viewPyramid = (ViewPyramid) super.clone();
        viewPyramid.setBackPlaneDistance(backPlaneDistance);
        viewPyramid.setFrontPlaneDistance(frontPlaneDistance);
        viewPyramid.setFrontPlaneWidth(frontPlaneWidth);
        viewPyramid.setFrontPlaneHeight(frontPlaneHeight);
        return viewPyramid;
    }
}
