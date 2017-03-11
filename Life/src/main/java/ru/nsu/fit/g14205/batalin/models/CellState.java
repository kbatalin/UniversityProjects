package ru.nsu.fit.g14205.batalin.models;

/**
 * Created by kir55rus on 27.02.17.
 */
public enum CellState {
    ALIVE,
    DEAD;

    public static CellState opposite(CellState state) {
        if (state == ALIVE) {
            return DEAD;
        }

        return ALIVE;
    }
}
