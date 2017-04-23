package ru.nsu.fit.g14205.batalin.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus) on 23.04.17.
 */
public class Scene implements Iterable<Segment> {
    private List<Segment> segments = new ArrayList<>();

    public Scene() {
    }

    public Scene(List<Segment> segments) {
        this.segments.addAll(segments);
    }

    public void addSegment(Segment segment) {
        segments.add(segment);
    }

    public

    public void clear() {
        segments.clear();
    }

    public Segment getSegment(int index) {
        return segments.get(index);
    }

    @Override
    public Iterator<Segment> iterator() {
        return segments.iterator();
    }
}
