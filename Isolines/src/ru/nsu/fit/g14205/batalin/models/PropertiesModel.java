package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.Observable;
import ru.nsu.fit.g14205.batalin.utils.observe.ObserveEvent;

import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public interface PropertiesModel extends Observable {
    DoubleBinaryOperator getFunction();
    void setFunction(DoubleBinaryOperator function);

    Area getArea();
    void setArea(Area area);

    int getValuesCount();
    void setValuesCount(int count);

    Color getValueColor(double value);
    Color[] getValuesColors();
    void setValuesColors(Color[] colors);

    double[] getValues();
    double getMinValue();
    double getMaxValue();

    enum Event implements ObserveEvent {
        FUNCTION_CHANGED,
        AREA_CHANGED,
        VALUES_CHANGED,
        COLORS_CHANGED,
    }
}
