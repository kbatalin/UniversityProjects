package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus) on 24.04.17.
 */
public class Figure extends ObservableBase implements PaintedFigure {
    private List<PaintedFigure> figures = new ArrayList<>();
    private List<Segment> segments = new ArrayList<>();
    private Parallelepiped outboardBox;
    private FigureProperties figureProperties;

    public Figure(FigureProperties figureProperties) {
        this(figureProperties, null, null);
    }

    public Figure(FigureProperties figureProperties, List<PaintedFigure> figures, List<Segment> segments) {
        addFigures(figures);
        addSegments(segments);
        updOutboardBox();
        this.figureProperties = figureProperties;

        CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();
        coordinateSystem.addObserver(CoordinateSystem.Event.ROTATION_CHANGED, this::updOutboardBox);
        coordinateSystem.addObserver(CoordinateSystem.Event.CENTER_CHANGED, this::updOutboardBox);
        if (figureProperties.getLineProperties() != null) {
            figureProperties.getLineProperties().addObserver(LineProperties.Event.COLOR_CHANGED, () -> {
                notifyObservers(Event.FIGURE_CHANGED);
            });
        }
    }

    @Override
    public PaintedFigure clone() throws CloneNotSupportedException {
        Figure figure = (Figure) super.clone();
        figure.figures = new ArrayList<>();
        for (PaintedFigure paintedFigure : figures) {
            figure.figures.add(paintedFigure.clone());
        }
        figure.segments = new ArrayList<>();
        for (Segment segment : segments) {
            figure.segments.add(segment.clone());
        }
        figure.figureProperties = figureProperties.clone();
        return figure;
    }

    @Override
    public FigureProperties getFigureProperties() {
        return figureProperties;
    }

    @Override
    public void addFigure(PaintedFigure figure) {
        if (figure == null) {
            return;
        }
        figure.addObserver(Event.FIGURE_CHANGED, this::updOutboardBox);
        figures.add(figure);
        updOutboardBox();
    }

    @Override
    public void addFigures(List<PaintedFigure> figures) {
        if (figures == null) {
            return;
        }
        for (PaintedFigure figure : figures) {
            figure.addObserver(Event.FIGURE_CHANGED, this::updOutboardBox);
            this.figures.add(figure);
        }
        updOutboardBox();
    }

    @Override
    public void addSegment(Segment segment) {
        segments.add(segment);
        updOutboardBox();
    }

    @Override
    public void addSegments(List<Segment> segments) {
        if (segments == null) {
            return;
        }
        this.segments.addAll(segments);
        updOutboardBox();
    }

    public Parallelepiped getOutboardBox() {
        return outboardBox;
    }

    public void clear() {
        figures.clear();
        segments.clear();
        updOutboardBox();
    }

    private void updOutboardBox() {
        Point3D min = null;
        Point3D max = null;

        for (Segment segment : segments) {
            min = Point3D.createMin(min, segment.getMinPoint());
            max = Point3D.createMax(max, segment.getMaxPoint());
        }

        for (PaintedFigure figure : figures) {
            Parallelepiped box = figure.getOutboardBox();
            if (box == null) {
                continue;
            }

            Point3D a = box.getPos();
            Point3D b = new Point3D(a.getX() + box.getWidth(), a.getY() + box.getHeight(), a.getZ() + box.getDepth());
            Matrix transformMatrix = figure.getFigureProperties().getCoordinateSystem().getTransformMatrix();
            a = new Point3D(transformMatrix.multiply(a.toMatrix4()).subMatrix(0, 0, 1, 3));
            b = new Point3D(transformMatrix.multiply(b.toMatrix4()).subMatrix(0, 0, 1, 3));

            Point3D figureMin = Point3D.createMin(a, b);
            Point3D figureMax = Point3D.createMax(a, b);
            min = Point3D.createMin(figureMin, min);
            max = Point3D.createMax(figureMax, max);
        }

        if (min == null || max == null) {
            outboardBox = null;
        } else {
            outboardBox = new Parallelepiped(min, max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ());
        }

        notifyObservers(Event.FIGURE_CHANGED);
    }

    @Override
    public Iterator<PaintedFigure> figures() {
        return figures.iterator();
    }

    @Override
    public Iterator<Segment> segments() {
        return segments.iterator();
    }
}
