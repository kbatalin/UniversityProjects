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

    public Point3D(Matrix matrix) {
        if (matrix.getWidth() != 1 || matrix.getHeight() != 3) {
            throw new IllegalArgumentException("Bad matrix size");
        }

        x = matrix.get(0, 0);
        y = matrix.get(0, 1);
        z = matrix.get(0, 2);
    }

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Matrix toMatrix3() {
        return new Matrix(1, 3, new double[]{x, y, z});
    }

    public Matrix toMatrix4() {
        return new Matrix(1, 4, new double[]{x, y, z, 1});
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

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public static Point3D createMin(Point3D a, Point3D b) {
        return new Point3D(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ())
        );
    }

    public static Point3D createMax(Point3D a, Point3D b) {
        return new Point3D(
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ())
        );
    }
}
