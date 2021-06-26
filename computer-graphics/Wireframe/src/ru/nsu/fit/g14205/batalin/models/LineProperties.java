package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * Created by kir55rus on 12.04.17.
 */
public interface LineProperties extends Observable, Cloneable {
    Color getColor();
    void setColor(Color color);

    int getControlPointId(Point2D pos);
    void setControlPoint(int id, Point2D pos);
    Iterator<Point2D> getControlPointsIterator();
    int getControlPointsCount();
    void addControlPoint(Point2D pos);
    void addControlPoint(int index, Point2D pos);
    void delControlPoint(int id);

    Point2D getPoint(double t);

    double getLength();

    Area getArea();

    LineProperties clone() throws CloneNotSupportedException;

    enum Event implements ObserveEvent {
        COLOR_CHANGED,
        CONTROL_POINTS_CHANGED,
    }
}
