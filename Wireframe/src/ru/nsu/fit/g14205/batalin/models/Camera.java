package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class Camera extends ObservableBase implements CameraProperties {
    private Point3D cameraPosition;
    private Point3D viewPoint;
    private Point3D upVector;

    public Camera() {
    }

    public Camera(Point3D cameraPosition, Point3D viewPoint, Point3D upVector) {
        this.cameraPosition = cameraPosition;
        this.viewPoint = viewPoint;
        this.upVector = upVector;
    }

    @Override
    public CameraProperties clone() throws CloneNotSupportedException {
        Camera camera = (Camera) super.clone();
        camera.cameraPosition = cameraPosition.clone();
        camera.viewPoint = viewPoint.clone();
        camera.upVector = upVector.clone();
        return camera;
    }

    @Override
    public Point3D getCameraPosition() {
        return cameraPosition;
    }

    @Override
    public void setCameraPosition(Point3D cameraPosition) {
        this.cameraPosition = cameraPosition;
        notifyObservers(Event.CAMERA_POSITION_CHANGED);
    }

    @Override
    public Point3D getViewPoint() {
        return viewPoint;
    }

    @Override
    public void setViewPoint(Point3D viewPoint) {
        this.viewPoint = viewPoint;
        notifyObservers(Event.VIEW_POINT_CHANGED);
    }

    @Override
    public Point3D getUpVector() {
        return upVector;
    }

    @Override
    public void setUpVector(Point3D upVector) {
        this.upVector = upVector;
        notifyObservers(Event.UP_VECTOR_CHANGED);
    }
}
