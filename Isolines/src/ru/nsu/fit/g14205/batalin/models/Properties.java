package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.ObservableBase;

import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public class Properties extends ObservableBase implements PropertiesModel {
    private Function function;
    private Area area;
    private double[] values;
    private Color[] colors;
    private double scale;

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

        double min = function.calc(area.first);
        double max = min;
        for(int x = area.first.x; x < area.second.x; ++x) {
            for(int y = area.first.y; y < area.second.y; ++y) {
                double value = function.calc(x, y);
                if (Double.compare(value, min) < 0) {
                    min = value;
                }
                if (Double.compare(max, value) < 0) {
                    max = value;
                }
            }
        }

        values = new double[valuesCount];
        double len = (max - min) / valuesCount;
        for(int i = 0; i < valuesCount; ++i) {
            values[i] = min + i * len;
        }
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
    public Function getFunction() {
        return function;
    }

    @Override
    public void setFunction(Function function) {
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

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;

        notifyObservers(Event.SCALE_CHANGED);
    }

    @Override
    public int getLegendWidth() {
        return 100;
    }
}
