package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.Observable;

import java.awt.*;

/**
 * Created by kir55rus on 27.02.17.
 */
public class PropertiesModel extends Observable implements IPropertiesModel {
    private static int DEFAULT_HEX_SIZE = 30;
    private static Dimension DEFAULT_FIELD_SIZE = new Dimension(20, 100);
    private static int DEFAULT_MIN_HEX_SIZE = 5;
    private static int DEFAULT_MAX_HEX_SIZE = 50;

    private int hexSize;
    private int hexIncircle;
    private Dimension fieldSize;

    public PropertiesModel() {
        this(DEFAULT_FIELD_SIZE, DEFAULT_HEX_SIZE);
    }

    public PropertiesModel(Dimension fieldSize, int hexSize) {
        setFieldSize(fieldSize);
        setHexSize(hexSize);
    }

    @Override
    public int getHexIncircle() {
        return hexIncircle;
    }

    @Override
    public int getHexSize() {
        return hexSize;
    }

    @Override
    public void setHexSize(int hexSize) {
        if (hexSize <= 0) {
            throw new IllegalArgumentException("Size <= 0");
        }
        this.hexSize = hexSize;
        this.hexIncircle = (int)(hexSize * Math.sqrt(3) / 2);

        notifyObservers(PropertiesModelEvent.SIZE_CHANGED);
    }

    @Override
    public Dimension getFieldSize() {
        return fieldSize;
    }

    @Override
    public void setFieldSize(Dimension fieldSize) {
        if (fieldSize.height <= 0 || fieldSize.width <= 0) {
            throw new IllegalArgumentException("fieldSize <= 0");
        }
        this.fieldSize = fieldSize;

        notifyObservers(PropertiesModelEvent.SIZE_CHANGED);
    }

    @Override
    public int getMinHexSize() {
        return DEFAULT_MIN_HEX_SIZE;
    }

    @Override
    public int getMaxHexSize() {
        return DEFAULT_MAX_HEX_SIZE;
    }
}
