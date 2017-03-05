package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.IObservable;

import java.awt.*;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface IField extends IObservable {
    Dimension getSize();
    boolean checkCrds(int x, int y);
    boolean checkCrds(Point pos);
    CellState get(int x, int y);
    CellState get(Point pos);
    void set(int x, int y, CellState cellState);
    void set(Point pos, CellState cellState);
    void clear();
    int getLivingCellsCount();
}
