package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

import javax.sound.sampled.Line;
import java.awt.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kirill Batalin (kir55rus) on 24.04.17.
 */
public interface PaintedFigure extends Observable, Cloneable {
    void addFigure(PaintedFigure figure);

    void addFigures(List<PaintedFigure> figures);

    void addSegment(Segment segment);

    void addSegments(List<Segment> segments);

    Parallelepiped getOutboardBox();

    void clear();

    Iterator<PaintedFigure> figures();

    Iterator<Segment> segments();

    FigureProperties getFigureProperties();

    PaintedFigure clone() throws CloneNotSupportedException;

    enum Event implements ObserveEvent {
        FIGURE_CHANGED,
    }
}
