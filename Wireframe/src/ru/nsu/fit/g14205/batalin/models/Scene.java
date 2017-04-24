package ru.nsu.fit.g14205.batalin.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class Scene {
    private List<Figure> figures = new ArrayList<>();

    public Scene() {
    }

    public Scene(List<Figure> figures) {
        this.figures.addAll(figures);
    }

    public void addFigure(Figure segment) {
        figures.add(segment);
        updOutboardBox();
    }

    public Parallelepiped getOutboardBox() {
        Point3D min = null;
        Point3D max = null;
        for (Figure figure : figures) {
            Parallelepiped box = figure.getOutboardBox();
            if (box == null) {
                continue;
            }

            Point3D a = box.getPos();
            Point3D b = new Point3D(a.getX() + box.getWidth(), a.getY() + box.getHeight(), a.getZ() + box.getDepth());
            Point3D figureMin = Point3D.createMin(a, b);
            Point3D figureMax = Point3D.createMax(a, b);
            min = Point3D.createMin(figureMin, min);
            max = Point3D.createMax(figureMax, max);
        }

        return new Parallelepiped(min, max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ());
    }

    public void clear() {
        figures.clear();
    }

//    public Segment getSegment(int index) {
//        return figures.get(index);
//    }

    private void updOutboardBox() {
//        if (figures.size() < 1) {
//            outboardBox = null;
//            return;
//        }
//
//        Segment firstSegment = figures.get(0);
//        Point3D min = firstSegment.getMinPoint();
//        Point3D max = firstSegment.getMaxPoint();
//
//        for (Segment segment : figures) {
//            min = Point3D.createMin(min, segment.getMinPoint());
//            max = Point3D.createMax(max, segment.getMaxPoint());
//        }
//
//        outboardBox = new Parallelepiped(min, max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ());
    }

//    @Override
//    public Iterator<Segment> iterator() {
//        return figures.iterator();
//    }
}
