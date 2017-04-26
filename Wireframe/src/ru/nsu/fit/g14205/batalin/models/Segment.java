package ru.nsu.fit.g14205.batalin.models;


/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class Segment implements Cloneable {
    private Point3D first;
    private Point3D second;

    public Segment() {
    }

    public Segment(Point3D first, Point3D second) {
        this.first = first;
        this.second = second;
    }

    public Point3D getFirst() {
        return first;
    }

    public void setFirst(Point3D first) {
        this.first = first;
    }

    public Point3D getSecond() {
        return second;
    }

    public void setSecond(Point3D second) {
        this.second = second;
    }

    public Point3D getMinPoint() {
        return new Point3D(
                Math.min(first.getX(), second.getX()),
                Math.min(first.getY(), second.getY()),
                Math.min(first.getZ(), second.getZ())
        );
    }

    public Point3D getMaxPoint() {
        return new Point3D(
                Math.max(first.getX(), second.getX()),
                Math.max(first.getY(), second.getY()),
                Math.max(first.getZ(), second.getZ())
        );
    }

    @Override
    public Segment clone() throws CloneNotSupportedException {
        Segment segment = (Segment) super.clone();
        segment.first = new Point3D(first.getX(), first.getY(), first.getZ());
        segment.second = new Point3D(second.getX(), second.getY(), second.getZ());
        return segment;
    }
}
