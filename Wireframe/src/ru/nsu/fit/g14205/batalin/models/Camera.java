package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class Camera extends ObservableBase implements CameraProperties {
    private Point3D cameraPosition;
    private Point3D viewPoint;
    private Point3D upVector;
    private Matrix worldToCamMatrix;

    public Camera() {
    }

    public Camera(Point3D cameraPosition, Point3D viewPoint, Point3D upVector) {
        this.cameraPosition = cameraPosition;
        this.viewPoint = viewPoint;
        this.upVector = upVector;
        updMatrix();
    }

    @Override
    public CameraProperties clone() throws CloneNotSupportedException {
        Camera camera = (Camera) super.clone();
        camera.setCameraPosition(cameraPosition.clone());
        camera.setViewPoint(viewPoint.clone());
        camera.setUpVector(upVector.clone());
        return camera;
    }

    private void updMatrix() {
        Point3D camPos = getCameraPosition();
        Point3D viewPoint = getViewPoint();
        Point3D up = getUpVector();

        Point3D zC = new Point3D(camPos.toMatrix3().deduct(viewPoint.toMatrix3()).normalize());
        Point3D xC = new Point3D(new Matrix(3, 3, new double[]{
                0, -up.getZ(), up.getY(),
                up.getZ(), 0, -up.getX(),
                -up.getY(), up.getX(), 0
        }).multiply(zC.toMatrix3()).normalize());
        Point3D yC = new Point3D(new Matrix(3, 3, new double[]{
                0, -zC.getZ(), zC.getY(),
                zC.getZ(), 0, -zC.getX(),
                -zC.getY(), zC.getX(), 0
        }).multiply(xC.toMatrix3()));

        Matrix shift = new Matrix(4, 4, new double[]{
                1, 0, 0, -camPos.getX(),
                0, 1, 0, -camPos.getY(),
                0, 0, 1, -camPos.getZ(),
                0, 0, 0, 1
        });

        Matrix MRotateCam = new Matrix(4, 4, new double[]{
                xC.getX(), xC.getY(), xC.getZ(), 0,
                yC.getX(), yC.getY(), yC.getZ(), 0,
                zC.getX(), zC.getY(), zC.getZ(), 0,
                0, 0, 0, 1
        });

        worldToCamMatrix = MRotateCam.multiply(shift);
    }

    @Override
    public Point3D getCameraPosition() {
        return cameraPosition;
    }

    @Override
    public void setCameraPosition(Point3D cameraPosition) {
        this.cameraPosition = cameraPosition;
        updMatrix();
        notifyObservers(Event.CAMERA_POSITION_CHANGED);
    }

    @Override
    public Point3D getViewPoint() {
        return viewPoint;
    }

    @Override
    public void setViewPoint(Point3D viewPoint) {
        this.viewPoint = viewPoint;
        updMatrix();
        notifyObservers(Event.VIEW_POINT_CHANGED);
    }

    @Override
    public Point3D getUpVector() {
        return upVector;
    }

    @Override
    public void setUpVector(Point3D upVector) {
        this.upVector = upVector;
        updMatrix();
        notifyObservers(Event.UP_VECTOR_CHANGED);
    }

    @Override
    public Matrix getWorldToCamMatrix() {
        return worldToCamMatrix;
    }
}
