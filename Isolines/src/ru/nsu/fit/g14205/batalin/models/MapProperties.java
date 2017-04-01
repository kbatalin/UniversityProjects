package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.ObservableBase;

import java.awt.*;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public class MapProperties extends PropertiesBase {
    private DoubleBinaryOperator function;
    private Area area;
    private double[] values;
    private Color[] colors;
    private double minValue;
    private double maxValue;

    @Override
    public int getValuesCount() {
        return values.length;
    }

    @Override
    public void setValuesCount(int count) {
        calcValues(count);

        notifyObservers(Event.VALUES_CHANGED);
    }

    private void calcValues(int valuesCount) {
        if (function == null || area == null) {
            return;
        }

        minValue = function.applyAsDouble(area.first.x, area.first.y);
        maxValue = minValue;
        for(int x = area.first.x; x < area.second.x; ++x) {
            for(int y = area.first.y; y < area.second.y; ++y) {
                double value = function.applyAsDouble(x, y);
                if (Double.compare(value, minValue) < 0) {
                    minValue = value;
                }
                if (Double.compare(maxValue, value) < 0) {
                    maxValue = value;
                }
            }
        }

        values = new double[valuesCount];
        double len = (maxValue - minValue) / (valuesCount + 1);
        for(int i = 0; i < valuesCount; ++i) {
            values[i] = minValue + (i + 1) * len;
        }
    }

    @Override
    public double getMinValue() {
        return minValue;
    }

    @Override
    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public Color[] getValuesColors() {
        return colors;
    }

    @Override
    public void setValuesColors(Color[] colors) {
        this.colors = colors;

        notifyObservers(Event.COLORS_CHANGED);
    }

    @Override
    public double[] getValues() {
        return values;
    }

    @Override
    public DoubleBinaryOperator getFunction() {
        return function;
    }

    @Override
    public void setFunction(DoubleBinaryOperator function) {
        this.function = function;
        if(values != null) {
            calcValues(values.length);
        }

        notifyObservers(Event.FUNCTION_CHANGED);
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public void setArea(Area area) {
        this.area = area;

        notifyObservers(Event.AREA_CHANGED);
    }
}
