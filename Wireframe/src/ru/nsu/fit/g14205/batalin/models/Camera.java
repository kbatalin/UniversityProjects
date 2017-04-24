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
        Point3D cam = getCameraPosition();
        Point3D camZ = new Point3D(cam.toMatrix3().deduct(getViewPoint().toMatrix3()).normalize());
        Point3D camY = new Point3D(getUpVector().toMatrix3().normalize());
        Point3D camX = new Point3D(new Matrix(3, 3, new double[]{
                0, -camY.getZ(), camY.getY(),
                camY.getZ(), 0, -camY.getX(),
                -camY.getY(), camY.getX(), 0,
        }).multiply(camZ.toMatrix3()).normalize());
        Matrix MRotateCam = new Matrix(3,3, new double[]{
                camX.getX(), camX.getY(), camX.getZ(),
                camY.getX(), camY.getY(), camY.getZ(),
                camZ.getX(), camZ.getY(), camZ.getZ(),
        });
        Matrix MOffsetCam = MRotateCam.multiply(cam.toMatrix3());

        worldToCamMatrix = new Matrix(4,4, new double[]{
                MRotateCam.get(0,0), MRotateCam.get(1,0), MRotateCam.get(2,0), -MOffsetCam.get(0,0),
                MRotateCam.get(0,1), MRotateCam.get(1,1), MRotateCam.get(2,1), -MOffsetCam.get(0,1),
                MRotateCam.get(0,2), MRotateCam.get(1,2), MRotateCam.get(2,2), -MOffsetCam.get(0,2),
                0, 0, 0, 1
        });
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
