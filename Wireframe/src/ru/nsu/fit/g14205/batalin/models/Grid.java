package ru.nsu.fit.g14205.batalin.models;

/**
 * Created by kir55rus on 12.04.17.
 */
public class Grid {
    public int cols;
    public int rows;

    public Grid(int cols, int rows) {
        if (!checkSize(cols) || !checkSize(rows)) {
            throw new IllegalArgumentException("Cols and rows must be > 0");
        }

        this.cols = cols;
        this.rows = rows;
    }

    public boolean checkSize(int value) {
        return value > 0;
    }
}
