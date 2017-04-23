package ru.nsu.fit.g14205.batalin.models;

import java.awt.geom.Point2D;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class Segment implements Cloneable {
    private Point2D first;
    private Point2D second;

    public Segment() {
    }

    public Segment(Point2D first, Point2D second) {
        this.first = first;
        this.second = second;
    }

    public Point2D getFirst() {
        return first;
    }

    public void setFirst(Point2D first) {
        this.first = first;
    }

    public Point2D getSecond() {
        return second;
    }

    public void setSecond(Point2D second) {
        this.second = second;
    }

    @Override
    public Segment clone() throws CloneNotSupportedException {
        Segment segment = (Segment) super.clone();
        segment.first = new Point2D.Double(first.getX(), first.getY());
        segment.second = new Point2D.Double(second.getX(), second.getY());
        return segment;
    }
}
