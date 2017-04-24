package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus) on 24.04.17.
 */
public interface PaintedFigure extends Observable {
    void addFigure(PaintedFigure figure);

    void addFigures(List<PaintedFigure> figures);

    void addSegment(Segment segment);

    void addSegments(List<Segment> segments);

    Parallelepiped getOutboardBox();

    void clear();

    CoordinateSystem getCoordinateSystem();

    Iterator<PaintedFigure> figures();

    Iterator<Segment> segments();

    enum Event implements ObserveEvent {
        FIGURE_CHANGED,
    }
}
