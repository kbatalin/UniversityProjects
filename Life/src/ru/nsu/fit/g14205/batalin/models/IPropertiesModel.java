package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.IObservable;

import java.awt.*;

/**
 * Created by kir55rus on 27.02.17.
 */
public interface IPropertiesModel extends IObservable {
    int getHexIncircle();
    int getHexSize();
    void setHexSize(int hexSize);
    Dimension getFieldSize();
    void setFieldSize(Dimension fieldSize);
}
