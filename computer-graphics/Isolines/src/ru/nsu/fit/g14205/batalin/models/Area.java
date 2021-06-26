package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by kir55rus on 01.04.17.
 */
public class Area implements Cloneable {
    public Point2D first;
    public Point2D second;

    public Area(Point2D first, Point2D second) {
        this(first.getX(), first.getY(), second.getX(), second.getY());
    }

    public Area(double x0, double y0, double x1, double y1) {
        first = new Point2D.Double(x0, y0);
        second = new Point2D.Double(x1, y1);
    }

    public Dimension toDimension() {
        double width = second.getX() - first.getX();
        double height = second.getY() - first.getY();

        return new Dimension((int)width, (int)height);
    }

    @Override
    protected Area clone() {
        return new Area(first.getX(), first.getY(), second.getX(), second.getY());
    }
}
