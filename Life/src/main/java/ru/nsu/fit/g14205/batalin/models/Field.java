package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by kir55rus on 27.02.17.
 */
public class Field implements IField {
    private ArrayList<ArrayList<CellState>> field;
    private Dimension fieldSize;
    private int livingCellsCount;

    public Field(Dimension fieldSize) {
        this.fieldSize = fieldSize;

        resetField();
    }

    private void resetField() {
        field = new ArrayList<>();

        for(int i = 0; i < fieldSize.width; ++i) {
            ArrayList<CellState> arr = new ArrayList<>();
            field.add(i, arr);

            for(int j = 0; j < fieldSize.height; ++j) {
                arr.add(j, CellState.DEAD);
            }
        }

        livingCellsCount = 0;
    }

    @Override
    public Dimension getSize() {
        return fieldSize;
    }

    @Override
    public boolean checkCrds(int x, int y) {
        return x >= 0 && x < fieldSize.width && y >= 0 && y < fieldSize.height;
    }

    @Override
    public boolean checkCrds(Point pos) {
        return checkCrds(pos.x, pos.y);
    }

    @Override
    public CellState get(int x, int y) {
        if(!checkCrds(x, y)) {
            throw new IndexOutOfBoundsException("Bad crds: (" + x + ", " + y + ")");
        }

        return field.get(x).get(y);
    }

    @Override
    public CellState get(Point pos) {
        return get(pos.x, pos.y);
    }

    @Override
    public void set(int x, int y, CellState cellState) {
        if(!checkCrds(x, y)) {
            throw new IndexOutOfBoundsException("Bad crds: (" + x + ", " + y + ")");
        }

        ArrayList<CellState> line = field.get(x);
        CellState oldState = line.get(y);

        if (oldState == cellState) {
            return;
        }

        if (cellState == CellState.ALIVE) {
            ++livingCellsCount;
        } else {
            --livingCellsCount;
        }

        line.set(y, cellState);
        field.set(x, line);
    }

    @Override
    public void set(Point pos, CellState cellState) {
        set(pos.x, pos.y, cellState);
    }

    @Override
    public void clear() {
        resetField();
    }

    @Override
    public int getLivingCellsCount() {
        return livingCellsCount;
    }
}
