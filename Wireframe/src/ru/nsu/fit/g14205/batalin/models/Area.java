package ru.nsu.fit.g14205.batalin.models;

import java.awt.geom.Point2D;

/**
 * Created by kir55rus on 12.04.17.
 */
public class Area {
    public Point2D first;
    public Point2D second;

    public Area(Point2D first, Point2D second) {
        this(first.getX(), first.getY(), second.getX(), second.getY());
    }

    public Area(double x1, double y1, double x2, double y2) {
        first = new Point2D.Double(x1, y1);
        second = new Point2D.Double(x2, y2);
    }

    public double getWidth() {
        return Math.abs(first.getX() - second.getX());
    }

    public double getHeight() {
        return Math.abs(first.getY() - second.getY());
    }
}
