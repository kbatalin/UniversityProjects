package ru.nsu.fit.g14205.batalin.models;

import java.awt.geom.Point2D;

/**
 * Created by kir55rus on 12.04.17.
 */
public class Area implements Cloneable {
    public Point2D first;
    public Point2D second;

    public Area(Point2D first, Point2D second) {
        this(first.getX(), first.getY(), second.getX(), second.getY());
    }

    public Area(double x1, double y1, double x2, double y2) {
        first = new Point2D.Double(x1, y1);
        second = new Point2D.Double(x2, y2);
    }

    @Override
    protected Area clone() throws CloneNotSupportedException {
        Area area = (Area) super.clone();
        area.first = new Point2D.Double(first.getX(), first.getY());
        area.second = new Point2D.Double(second.getX(), second.getY());
        return area;
    }

    public double getWidth() {
        return Math.abs(second.getX() - first.getX());
    }

    public double getHeight() {
        return Math.abs(second.getY() - first.getY());
    }
}
