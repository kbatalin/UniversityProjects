package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * Created by kir55rus on 12.04.17.
 */
public interface LineProperties extends Observable {
    Color getColor();
    void setColor(Color color);

    int getControlPointId(Point2D pos);
    void setControlPoint(int id, Point2D pos);
    Iterator<Point2D> getControlPointsIterator();
    void addControlPoint(Point2D pos);
    void delControlPoint(int id);

    Point2D getPoint(double t);

    Area getArea();

    enum Event implements ObserveEvent {
        COLOR_CHANGED,
        CONTROL_POINTS_CHANGED,
    }
}
