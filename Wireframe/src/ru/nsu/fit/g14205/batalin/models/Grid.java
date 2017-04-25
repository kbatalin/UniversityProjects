package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

/**
 * Created by kir55rus on 12.04.17.
 */
public class Grid extends ObservableBase implements Observable, Cloneable {
    private int cols;
    private int rows;
    private int segmentSplitting;

    public enum Event implements ObserveEvent {
        SIZE_CHANGED,
        SEGMENT_SPLITTING_CHANGED,
    }

    public Grid() {
    }

    public Grid(int cols, int rows, int segmentSplitting) {
        if (!checkSize(cols) || !checkSize(rows)) {
            throw new IllegalArgumentException("Cols and rows must be > 0");
        }

        this.cols = cols;
        this.rows = rows;
        this.segmentSplitting = segmentSplitting;
    }

    public int getSegmentSplitting() {
        return segmentSplitting;
    }

    public void setSegmentSplitting(int segmentSplitting) {
        this.segmentSplitting = segmentSplitting;
        notifyObservers(Event.SEGMENT_SPLITTING_CHANGED);
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
        notifyObservers(Event.SIZE_CHANGED);
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
        notifyObservers(Event.SIZE_CHANGED);
    }

    public boolean checkSize(int value) {
        return value > 0;
    }

    @Override
    public Grid clone() throws CloneNotSupportedException {
        Grid grid = (Grid) super.clone();
        grid.rows = this.rows;
        grid.cols = this.cols;
        return grid;
    }
}
