package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;

/**
 * Created by kir55rus on 27.02.17.
 */
public class PropertiesModel extends ModelBase {
    private static int DEFAULT_HEX_SIZE = 30;

    private int hexSize;
    private int hexIncircle;

    public PropertiesModel() {
        this(DEFAULT_HEX_SIZE);
    }

    public PropertiesModel(int hexSize) {
        setHexSize(hexSize);
    }

    public int getHexIncircle() {
        return hexIncircle;
    }

    public int getHexSize() {
        return hexSize;
    }

    public void setHexSize(int hexSize) {
        if (hexSize <= 0) {
            throw new IllegalArgumentException("Size <= 0");
        }
        this.hexSize = hexSize;
        this.hexIncircle = (int)(hexSize * Math.sqrt(3) / 2);
    }

}
