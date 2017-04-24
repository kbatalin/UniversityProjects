package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus) on 24.04.17.
 */
public class CoordinateSystem extends ObservableBase implements Observable, Cloneable {
    private Point3D center;
    private Matrix rotation;

    public enum Event implements ObserveEvent {
        CENTER_CHANGED,
        ROTATION_CHANGED,
    }

    public CoordinateSystem() {
        this(new Point3D(0, 0, 0),
                new Matrix(3, 3, new double[]{
                1, 0, 0,
                0, 1, 0,
                0, 0, 1
        }));
    }

    public CoordinateSystem(Point3D center, Matrix rotation) {
        this.center = center;
        this.rotation = rotation;
    }

    @Override
    public CoordinateSystem clone() throws CloneNotSupportedException {
        CoordinateSystem coordinateSystem = (CoordinateSystem) super.clone();
        coordinateSystem.center = center.clone();
        coordinateSystem.rotation.clone();
        return coordinateSystem;
    }

    public Point3D getCenter() {
        return center;
    }

    public void setCenter(Point3D center) {
        this.center = center;
        notifyObservers(Event.CENTER_CHANGED);
    }

    public Matrix getRotation() {
        return rotation;
    }

    public void setRotation(Matrix rotation) {
        this.rotation = rotation;
        notifyObservers(Event.ROTATION_CHANGED);
    }

    public Matrix getTransformMatrix() {
        return new Matrix(4, 4, new double[]{
                rotation.get(0, 0), rotation.get(1, 0), rotation.get(2, 0), center.getX(),
                rotation.get(0, 1), rotation.get(1, 1), rotation.get(2, 1), center.getY(),
                rotation.get(0, 2), rotation.get(1, 2), rotation.get(2, 2), center.getZ(),
                0, 0, 0, 1
        });
    }
}
