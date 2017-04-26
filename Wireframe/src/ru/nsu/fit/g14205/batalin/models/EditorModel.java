package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

import java.awt.geom.Point2D;

/**
 * Created by kir55rus on 12.04.17.
 */
public class EditorModel extends ObservableBase implements Observable {
    private int zoom;
    private Point2D offset;
    private int currentFigure;
    private ApplicationProperties applicationProperties;

    public enum Event implements ObserveEvent {
        ZOOM_CHANGED,
        ACTIVE_LINE_CHANGED,
        OFFSET_CHANGED,
    }

    public EditorModel(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        zoom = 100;
        currentFigure = 0;
        offset = new Point2D.Double(0, 0);
    }

    public Point2D getOffset() {
        return offset;
    }

    public void setOffset(Point2D offset) {
        this.offset = offset;
        notifyObservers(Event.OFFSET_CHANGED);
    }

    public int getDefaultSize() {
        return 30;
    }

    public int getMaxZoom() {
        return 500;
    }

    public int getMinZoom() {
        return 10;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
        notifyObservers(Event.ZOOM_CHANGED);
    }

    public int getCurrentFigure() {
        return currentFigure;
    }

    public void setCurrentFigure(int currentFigure) {
        if (currentFigure < 0 || currentFigure >= applicationProperties.getFigurePropertiesCount()) {
            throw new IllegalArgumentException("Bad active line index");
        }

        this.currentFigure = currentFigure;
        notifyObservers(Event.ACTIVE_LINE_CHANGED);
    }
}
