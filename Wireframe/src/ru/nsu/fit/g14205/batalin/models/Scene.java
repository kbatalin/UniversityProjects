package ru.nsu.fit.g14205.batalin.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class Scene implements Iterable<Segment> {
    private List<Segment> segments = new ArrayList<>();
    private Parallelepiped outboardBox;

    public Scene() {
    }

    public Scene(List<Segment> segments) {
        this.segments.addAll(segments);
        updOutboardBox();
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
        updOutboardBox();
    }

    public Parallelepiped getOutboardBox() {
        return outboardBox;
    }

    public void clear() {
        segments.clear();
        updOutboardBox();
    }

    public Segment getSegment(int index) {
        return segments.get(index);
    }

    private void updOutboardBox() {
        if (segments.size() < 1) {
            outboardBox = null;
            return;
        }

        Segment firstSegment = segments.get(0);
        Point3D min = firstSegment.getMinPoint();
        Point3D max = firstSegment.getMaxPoint();

        for (Segment segment : segments) {
            min = Point3D.createMin(min, segment.getMinPoint());
            max = Point3D.createMax(max, segment.getMaxPoint());
        }

        outboardBox = new Parallelepiped(min, max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ());
    }

    @Override
    public Iterator<Segment> iterator() {
        return segments.iterator();
    }
}
