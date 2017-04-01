package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.ObservableBase;

import java.awt.*;
import java.util.Arrays;

/**
 * Created by kir55rus on 01.04.17.
 */
public class LegendProperties extends PropertiesBase {
    private PropertiesModel mapProperties;
    private Function function;

    public LegendProperties(PropertiesModel mapProperties) {
        this.mapProperties = mapProperties;

        updFunction();

        mapProperties.addObserver(Event.AREA_CHANGED, this::updFunction);
        mapProperties.addObserver(Event.COLORS_CHANGED, this::updFunction);
        mapProperties.addObserver(Event.VALUES_CHANGED, this::updFunction);
        mapProperties.addObserver(Event.FUNCTION_CHANGED, this::updFunction);
    }

    @Override
    public Function getFunction() {
        return function;
    }

    @Override
    public void setFunction(Function function) {
    }

    @Override
    public Area getArea() {
        return mapProperties.getArea();
    }

    @Override
    public void setArea(Area area) {
    }

    @Override
    public int getValuesCount() {
        return mapProperties.getValuesCount();
    }

    @Override
    public void setValuesCount(int count) {
    }

    private void updFunction() {
//        function = (a, b) -> {
//            double[] values = mapProperties.getValues();
//            double len = (values[values.length - 1] - values[0]) / values.length;
//            double dx = mapProperties.getArea().toDimension().width / values.length;
//            return values[0] + ((int) (a / dx)) * len;
//        };

        Area area = mapProperties.getArea();
        double x0 = area.first.x;
        double y0 = mapProperties.getMinValue();
        double x1 = area.second.x;
        double y1 = mapProperties.getMaxValue();
        double k = (y1 - y0) / (x1 - x0);
        double b = y0 - k * x0;

        function = (x, y) -> {
            return x * k + b;
        };
    }

    @Override
    public Color[] getValuesColors() {
        return mapProperties.getValuesColors();
    }

    @Override
    public void setValuesColors(Color[] colors) {
    }

    @Override
    public double[] getValues() {
        return mapProperties.getValues();
    }

    @Override
    public double getMinValue() {
        return mapProperties.getMinValue();
    }

    @Override
    public double getMaxValue() {
        return mapProperties.getMaxValue();
    }
}
