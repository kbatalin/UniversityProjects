package ru.nsu.fit.g14205.batalin.models;


import ru.nsu.fit.g14205.batalin.utils.observe.Observable;

import java.awt.*;

/**
 * Created by kir55rus on 20.02.17.
 */
public class FieldModel extends Observable implements IFieldModel {
    private IField[] fields;
    private int activeField;
    private IPropertiesModel propertiesModel;
    private Point[][] firstOffsets;
    private Point[][] secondOffsets;

    public FieldModel(IPropertiesModel propertiesModel) {
        this.propertiesModel = propertiesModel;
        this.activeField = 0;

        fields = new IField[]{new Field(propertiesModel.getFieldSize()), new Field(propertiesModel.getFieldSize())};

        firstOffsets = new Point[2][6];
        secondOffsets = new Point[2][6];
        firstOffsets[0][0] = new Point(-1, -1);
        firstOffsets[0][1] = new Point(0, -1);
        firstOffsets[0][2] = new Point(1, 0);
        firstOffsets[0][3] = new Point(0, 1);
        firstOffsets[0][4] = new Point(-1, 1);
        firstOffsets[0][5] = new Point(-1, 0);

        secondOffsets[0][0] = new Point(0, -2);
        secondOffsets[0][1] = new Point(1, -1);
        secondOffsets[0][2] = new Point(1, 1);
        secondOffsets[0][3] = new Point(0, 2);
        secondOffsets[0][4] = new Point(-2,1);
        secondOffsets[0][5] = new Point(-2, -1);

        firstOffsets[1][0] = new Point(0, -1);
        firstOffsets[1][1] = new Point(1, -1);
        firstOffsets[1][2] = new Point(1, 0);
        firstOffsets[1][3] = new Point(1, 1);
        firstOffsets[1][4] = new Point(0, 1);
        firstOffsets[1][5] = new Point(-1, 0);

        secondOffsets[1][0] = new Point(0, -2);
        secondOffsets[1][1] = new Point(2, -1);
        secondOffsets[1][2] = new Point(2, 1);
        secondOffsets[1][3] = new Point(0, 2);
        secondOffsets[1][4] = new Point(-1, 1);
        secondOffsets[1][5] = new Point(-1, -1);
    }

    @Override
    public IField getActiveField() {
        return fields[activeField];
    }

    @Override
    public void step() {
        double liveBegin = propertiesModel.getLiveBegin();
        double birthBegin = propertiesModel.getBirthBegin();
        double birthEnd =  propertiesModel.getBirthEnd();
        double liveEnd = propertiesModel.getLiveEnd();

        int nextField = (activeField + 1) % fields.length;

        Dimension fieldSize = fields[activeField].getSize();
        for(Point pos = new Point(0, 0); pos.y < fieldSize.height; ++pos.y) {
            for(pos.x = 0; pos.x < fieldSize.width; ++pos.x) {
                double impact = getImpact(pos);

                if (Double.compare(impact, birthBegin) > 0 && Double.compare(impact, birthEnd) < 0) {
                    fields[nextField].set(pos, CellState.ALIVE);
                } else if (Double.compare(impact, liveBegin) < 0 || Double.compare(impact, liveEnd) > 0) {
                    fields[nextField].set(pos, CellState.DEAD);
                } else {
                    fields[nextField].set(pos, fields[activeField].get(pos));
                }
            }
        }

        activeField = nextField;
        notifyObservers(FieldModelEvent.FIELD_UPDATED);
    }

    @Override
    public double getImpact(int x, int y) {
        return getImpact(new Point(x, y));
    }

    @Override
    public double getImpact(Point pos) {
        int even = pos.y % 2;
        int firstCount = getAliveCellsCount(pos, firstOffsets[even]);
        int secondCount = getAliveCellsCount(pos, secondOffsets[even]);

        double firstImpact = propertiesModel.getFirstImpact();
        double secondImpact = propertiesModel.getSecondImpact();

        return firstCount * firstImpact + secondCount * secondImpact;
    }

    private int getAliveCellsCount(Point pos, Point[] offsets) {
        IField field = fields[activeField];
        int count = 0;

        for(Point offset : offsets) {
            Point neighbour = new Point(pos.x + offset.x, pos.y + offset.y);
            if (field.checkCrds(neighbour) && field.get(neighbour) == CellState.ALIVE) {
                ++count;
            }
        }

        return count;
    }
}
