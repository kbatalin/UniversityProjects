package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.Observable;
import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;
import ru.nsu.fit.g14205.batalin.models.observe.ObserveEvent;

/**
 * Created by kir55rus on 12.04.17.
 */
public class Grid extends ObservableBase implements Observable {
    private int cols;
    private int rows;

    public enum Event implements ObserveEvent {
        SIZE_CHANGED,
    }

    public Grid(int cols, int rows) {
        if (!checkSize(cols) || !checkSize(rows)) {
            throw new IllegalArgumentException("Cols and rows must be > 0");
        }

        this.cols = cols;
        this.rows = rows;
    }

    public Grid() {
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
}
