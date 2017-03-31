package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.Observable;
import ru.nsu.fit.g14205.batalin.utils.observe.ObserveEvent;

import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public interface PropertiesModel extends Observable {
    Function getFunction();
    void setFunction(Function function);

    Area getArea();
    void setArea(Area area);

    int getValuesCount();
    void setValuesCount(int count);

    Color[] getValuesColors();
    void setValuesColors(Color[] colors);

    double[] getValues();

    double getScale();
    void setScale(double scale);

    int getLegendWidth();

    enum Event implements ObserveEvent {
        FUNCTION_CHANGED,
        AREA_CHANGED,
        VALUES_CHANGED,
        COLORS_CHANGED,
        SCALE_CHANGED,
    }
}
