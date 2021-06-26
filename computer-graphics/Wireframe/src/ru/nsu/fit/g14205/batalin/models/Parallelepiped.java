package ru.nsu.fit.g14205.batalin.models;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class Parallelepiped implements Cloneable {
    private Point3D pos;
    private double width;
    private double height;
    private double depth;

    public Parallelepiped() {
    }

    public Parallelepiped(Point3D pos, double width, double height, double depth) {
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    protected Parallelepiped clone() throws CloneNotSupportedException {
        Parallelepiped parallelepiped = (Parallelepiped) super.clone();
        parallelepiped.pos = pos.clone();
        parallelepiped.width = width;
        parallelepiped.height = height;
        parallelepiped.depth = depth;
        return parallelepiped;
    }

    @Override
    public String toString() {
        return "Pos: " + pos.toString() + ", width: " + width + ", height: " + height + ", depth: " + depth;
    }

    public Point3D getPos() {
        return pos;
    }

    public void setPos(Point3D pos) {
        this.pos = pos;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }
}
