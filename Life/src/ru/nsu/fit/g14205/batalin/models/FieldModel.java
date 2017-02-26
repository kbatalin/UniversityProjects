package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by kir55rus on 20.02.17.
 */
public class FieldModel extends ModelBase {
    private Field[] fields;
    private int activeField;

    public class Field {
        private ArrayList<ArrayList<CellType>> field;
        private Dimension fieldSize;

        public Field(Dimension fieldSize) {
            this.fieldSize = fieldSize;

            field = new ArrayList<>();

            for(int i = 0; i < fieldSize.width; ++i) {
                ArrayList<CellType> arr = new ArrayList<>();
                field.add(i, arr);

                for(int j = 0; j < fieldSize.height; ++j) {
                    arr.add(j, CellType.DEAD);
                }
            }
        }

        public Dimension getSize() {
            return fieldSize;
        }

        public boolean checkCrds(int x, int y) {
            return x >= 0 && x < fieldSize.width && y >= 0 && y < fieldSize.height;
        }

        public boolean checkCrds(Point pos) {
            return checkCrds(pos.x, pos.y);
        }

        public CellType get(int x, int y) {
            if(!checkCrds(x, y)) {
                throw new IndexOutOfBoundsException("Bad crds: (" + x + ", " + y + ")");
            }

            return field.get(x).get(y);
        }

        public CellType get(Point pos) {
            return get(pos.x, pos.y);
        }

        public void set(int x, int y, CellType cellType) {
            if(!checkCrds(x, y)) {
                throw new IndexOutOfBoundsException("Bad crds: (" + x + ", " + y + ")");
            }

            ArrayList<CellType> line = field.get(x);
            line.set(y, cellType);
            field.set(x, line);
        }

        public void set(Point pos, CellType cellType) {
            set(pos.x, pos.y, cellType);
        }
    }

    public enum CellType {
        ALIVE,
        DEAD,
    }

    public FieldModel(Dimension fieldSize) {
        activeField = 0;
        fields = new Field[]{new Field(fieldSize), new Field(fieldSize)};

        getField().set(5, 5, CellType.ALIVE);
        getField().set(6, 5, CellType.ALIVE);
        getField().set(6, 6, CellType.ALIVE);
    }

    public Field getField() {
        return fields[activeField];
    }
}
