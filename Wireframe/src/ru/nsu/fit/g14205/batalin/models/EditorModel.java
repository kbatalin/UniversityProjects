package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

/**
 * Created by kir55rus on 12.04.17.
 */
public class EditorModel extends ObservableBase implements Observable {
    private int zoom;
    private int currentLine;
    private ApplicationProperties applicationProperties;

    public enum Event implements ObserveEvent {
        ZOOM_CHANGED,
        ACTIVE_LINE_CHANGED,
    }

    public EditorModel(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        zoom = 100;
        currentLine = 0;
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

    public int getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(int currentLine) {
        if (currentLine < 0 || currentLine >= applicationProperties.getLineProperties().size()) {
            throw new IllegalArgumentException("Bad active line index");
        }

        this.currentLine = currentLine;
        notifyObservers(Event.ACTIVE_LINE_CHANGED);
    }
}
