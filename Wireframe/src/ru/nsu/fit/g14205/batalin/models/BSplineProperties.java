package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Created by kir55rus on 12.04.17.
 */
public class BSplineProperties extends ObservableBase implements LineProperties {
    private ApplicationProperties applicationProperties;
    private Color color;
    private List<Point2D> controlPoints;
    private List<Double> segmentsLengths;
    private double length;
    private Matrix matrix;
    private Area area;

    public BSplineProperties(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;

        matrix = new Matrix(4, 4, new double[]{-1, 3, -3, 1, 3, -6, 3, 0, -3, 0, 3, 0, 1, 4, 1, 0});
        matrix = matrix.multiply(1 / 6.);

        controlPoints = new ArrayList<>();
        segmentsLengths = new ArrayList<>();
        area = new Area(0, 0, 0, 0);
        color = Color.BLUE;
    }

    @Override
    public LineProperties clone() throws CloneNotSupportedException {
        BSplineProperties bSplineProperties = (BSplineProperties) super.clone();
        bSplineProperties.applicationProperties = applicationProperties;
        bSplineProperties.color = new Color(color.getRGB());
        bSplineProperties.length = length;
        bSplineProperties.controlPoints = new ArrayList<>();
        for (Point2D point : controlPoints) {
            bSplineProperties.controlPoints.add(new Point2D.Double(point.getX(), point.getY()));
        }
        bSplineProperties.segmentsLengths = new ArrayList<>();
        bSplineProperties.segmentsLengths.addAll(segmentsLengths);
        bSplineProperties.matrix = matrix.clone();
        bSplineProperties.area = area.clone();
        return bSplineProperties;
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
        notifyObservers(Event.COLOR_CHANGED);
    }

    @Override
    public int getControlPointId(Point2D pos) {
        double controlPointRadius = applicationProperties.getControlPointRadius();
        double minDistance = controlPointRadius + 1;
        int index = -1;

        for(int i = 0; i < controlPoints.size(); ++i) {
            Point2D controlPoint = controlPoints.get(i);
            double distance = controlPoint.distance(pos);
            if (Double.compare(distance, controlPointRadius) <= 0 && Double.compare(distance, minDistance) < 0) {
                minDistance = distance;
                index = i;
            }
        }

        return index;
    }

    @Override
    public int getControlPointsCount() {
        return controlPoints.size();
    }

    @Override
    public void setControlPoint(int id, Point2D pos) {
        controlPoints.set(id, pos);
        afterControlPointChanging();
        notifyObservers(Event.CONTROL_POINTS_CHANGED);
    }

    @Override
    public Iterator<Point2D> getControlPointsIterator() {
        return controlPoints.iterator();
    }

    @Override
    public void addControlPoint(Point2D pos) {
        controlPoints.add(pos);
        afterControlPointChanging();
        notifyObservers(Event.CONTROL_POINTS_CHANGED);
    }

    @Override
    public void addControlPoint(int index, Point2D pos) {
        controlPoints.add(index, pos);
        afterControlPointChanging();
        notifyObservers(Event.CONTROL_POINTS_CHANGED);
    }

    @Override
    public void delControlPoint(int id) {
        controlPoints.remove(id);
        afterControlPointChanging();
        notifyObservers(Event.CONTROL_POINTS_CHANGED);
    }

    @Override
    public Point2D getPoint(double t) {
        t = Math.max(0., Math.min(1., t));

        if (controlPoints.size() < 4) {
            return null;
        }

        double tLength = t * length;
        int segmentIndex = 1;
        double currentLength = 0.;
        for(; segmentIndex < controlPoints.size() - 3; ++segmentIndex) {
            double nextLength = currentLength + segmentsLengths.get(segmentIndex);
            if(Double.compare(tLength, nextLength) <= 0) {
                break;
            }
            currentLength = nextLength;
        }

        double tSegment = (tLength - currentLength) / segmentsLengths.get(segmentIndex);
        return getPoint(segmentIndex, tSegment);
    }

    @Override
    public double getLength() {
        return length;
    }

    private Point2D getPoint(int segment, double t) {
        t = Math.max(0., Math.min(1., t));

        if (segment < 1 || segment > controlPoints.size() - 3) {
            return null;
        }

        Point2D p1 = controlPoints.get(segment - 1);
        Point2D p2 = controlPoints.get(segment);
        Point2D p3 = controlPoints.get(segment + 1);
        Point2D p4 = controlPoints.get(segment + 2);
        Matrix controlPointsMatrixX = new Matrix(1, 4, new double[]{p1.getX(), p2.getX(), p3.getX(), p4.getX()});
        Matrix controlPointsMatrixY = new Matrix(1, 4, new double[]{p1.getY(), p2.getY(), p3.getY(), p4.getY()});

        double tt = t * t;
        double ttt = tt * t;
        Matrix tMatrix = new Matrix(4, 1, new double[]{ttt, tt, t, 1.});

        Matrix tmMatrix = tMatrix.multiply(matrix);
        double x = tmMatrix.multiply(controlPointsMatrixX).toDouble();
        double y = tmMatrix.multiply(controlPointsMatrixY).toDouble();

        return new Point2D.Double(x, y);
    }

    private void afterControlPointChanging() {
        updLength();
        updArea();
    }

    private void updArea() {
        if (controlPoints.size() == 0) {
            area = new Area(0, 0, 0, 0);
            return;
        }
        double x0 = controlPoints.get(0).getX();
        double y0 = controlPoints.get(0).getY();
        double x1 = x0;
        double y1 = y0;

        for (Point2D pos : controlPoints) {
            if (Double.compare(pos.getX(), x0) < 0) {
                x0 = pos.getX();
            }
            if (Double.compare(pos.getX(), x1) > 0) {
                x1 = pos.getX();
            }
            if (Double.compare(pos.getY(), y0) < 0) {
                y0 = pos.getY();
            }
            if (Double.compare(pos.getY(), y1) > 0) {
                y1 = pos.getY();
            }
        }

        area = new Area(x0, y0, x1, y1);
    }

    private void updLength() {
        segmentsLengths = new ArrayList<>();
        length = 0.;
        for(int i = 0; i < controlPoints.size() - 1; ++i) {
            double dif = 1. / 100;

            double distance = 0.;
            Point2D startPoint = getPoint(i, 0.);
            if (startPoint == null) {
                segmentsLengths.add(0.);
                continue;
            }
            for(int part = 0; part <= 100; ++part) {
                Point2D tmpPoint = getPoint(i, dif * part);
                if (tmpPoint == null) {
                    break;
                }
                distance += startPoint.distance(tmpPoint);
                startPoint = tmpPoint;
            }
            segmentsLengths.add(distance);
            length += distance;
        }
    }
}
