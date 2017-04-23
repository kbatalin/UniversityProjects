package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public interface ViewPyramidProperties extends Observable, Cloneable {
    double getBackPlaneDistance();
    void setBackPlaneDistance(double distance);

    double getFrontPlaneDistance();
    void setFrontPlaneDistance(double distance);

    double getFrontPlaneWidth();
    void setFrontPlaneWidth(double width);

    double getFrontPlaneHeight();
    void setFrontPlaneHeight(double height);

    ViewPyramidProperties clone() throws CloneNotSupportedException;

    enum Event implements ObserveEvent {
        BACK_PLANE_DISTANCE_CHANGED,
        FRONT_PLANE_DISTANCE_CHANGED,
        FRONT_PLANE_SIZE_CHANGED,
    }

}
