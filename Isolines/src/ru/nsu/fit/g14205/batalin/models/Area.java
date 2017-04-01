package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public class Area implements Cloneable {
    public Point first;
    public Point second;

    public Area(Point first, Point second) {
        this(first.x, first.y, second.x, second.y);
    }

    public Area(int x0, int y0, int x1, int y1) {
        first = new Point(x0, y0);
        second = new Point(x1, y1);
    }

    public Dimension toDimension() {
        int width = second.x - first.x;
        int height = second.y - first.y;

        return new Dimension(width, height);
    }

    @Override
    protected Area clone() {
        return new Area(first.x, first.y, second.x, second.y);
    }
}
