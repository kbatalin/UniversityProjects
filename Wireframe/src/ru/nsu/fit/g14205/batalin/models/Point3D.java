package ru.nsu.fit.g14205.batalin.models;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class Point3D implements Cloneable {
    private double x;
    private double y;
    private double z;

    @Override
    public Point3D clone() throws CloneNotSupportedException {
        Point3D point3D = (Point3D) super.clone();
        point3D.x = x;
        point3D.y = y;
        point3D.z = z;
        return point3D;
    }

    public Point3D() {
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
