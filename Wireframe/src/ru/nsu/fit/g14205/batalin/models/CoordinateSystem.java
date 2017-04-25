package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus) on 24.04.17.
 */
public class CoordinateSystem extends ObservableBase implements Observable, Cloneable {
    private Point3D center;
    private double alphaAngle;
    private double betaAngle;
    private double thetaAngle;
    private Matrix transformMatrix;

    public enum Event implements ObserveEvent {
        CENTER_CHANGED,
        ROTATION_CHANGED,
    }

    public CoordinateSystem() {
        this(new Point3D(0, 0, 0), 0, 0, 0);
    }

    public CoordinateSystem(Point3D center, double alphaAngle, double betaAngle, double thetaAngle) {
        this.center = center;
        this.alphaAngle = alphaAngle;
        this.betaAngle = betaAngle;
        this.thetaAngle = thetaAngle;
        updTransformMatrix();
    }

    @Override
    public CoordinateSystem clone() throws CloneNotSupportedException {
        CoordinateSystem coordinateSystem = (CoordinateSystem) super.clone();
        coordinateSystem.setCenter(center.clone());
        coordinateSystem.setAlphaAngle(alphaAngle);
        coordinateSystem.setBetaAngle(betaAngle);
        coordinateSystem.setThetaAngle(thetaAngle);
        return coordinateSystem;
    }

    public Point3D getCenter() {
        return center;
    }

    public double getAlphaAngle() {
        return alphaAngle;
    }

    public void setAlphaAngle(double alphaAngle) {
        this.alphaAngle = alphaAngle;
        updTransformMatrix();
        notifyObservers(Event.ROTATION_CHANGED);
    }

    public double getBetaAngle() {
        return betaAngle;
    }

    public void setBetaAngle(double betaAngle) {
        this.betaAngle = betaAngle;
        updTransformMatrix();
        notifyObservers(Event.ROTATION_CHANGED);
    }

    public double getThetaAngle() {
        return thetaAngle;
    }

    public void setThetaAngle(double thetaAngle) {
        this.thetaAngle = thetaAngle;
        updTransformMatrix();
        notifyObservers(Event.ROTATION_CHANGED);
    }

    private void updTransformMatrix() {
        Matrix offset = new Matrix(4, 4, new double[]{
                1, 0, 0, center.getX(),
                0, 1, 0, center.getY(),
                0, 0, 1, center.getZ(),
                0, 0, 0, 1
        });

        double xCos = Math.cos(alphaAngle);
        double xSin = Math.sin(alphaAngle);
        Matrix xRotate = new Matrix(4, 4, new double[]{
                1, 0, 0, 0,
                0, xCos, -xSin, 0,
                0, xSin, xCos, 0,
                0, 0, 0, 1
        });

        double yCos = Math.cos(betaAngle);
        double ySin = Math.sin(betaAngle);
        Matrix yRotate = new Matrix(4, 4, new double[]{
                yCos, 0, ySin, 0,
                0, 1, 0, 0,
                -ySin, 0, yCos, 0,
                0, 0, 0, 1
        });

        double zCos = Math.cos(thetaAngle);
        double zSin = Math.sin(thetaAngle);
        Matrix zRotate = new Matrix(4, 4, new double[]{
                zCos, -zSin, 0, 0,
                zSin, zCos, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        });

        transformMatrix = offset.multiply(zRotate.multiply(yRotate.multiply(xRotate)));
    }

    public void setCenter(Point3D center) {
        this.center = center;
        updTransformMatrix();
        notifyObservers(Event.CENTER_CHANGED);
    }

    public Matrix getTransformMatrix() {
        return transformMatrix;
    }
}
