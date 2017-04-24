package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public interface CameraProperties extends Observable, Cloneable {
    Point3D getCameraPosition();

    void setCameraPosition(Point3D position);

    Point3D getViewPoint();

    void setViewPoint(Point3D viewPoint);

    Point3D getUpVector();

    void setUpVector(Point3D upVector);

    Matrix getWorldToCamMatrix();

    CameraProperties clone() throws CloneNotSupportedException;

    enum Event implements ObserveEvent {
        CAMERA_POSITION_CHANGED,
        VIEW_POINT_CHANGED,
        UP_VECTOR_CHANGED,
    }
}
