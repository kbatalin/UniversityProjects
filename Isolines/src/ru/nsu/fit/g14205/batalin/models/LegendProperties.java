package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public class LegendProperties extends FunctionPropertiesBase {
    private FunctionProperties mapProperties;
    private DoubleBinaryOperator function;

    public LegendProperties(FunctionProperties mapProperties) {
        this.mapProperties = mapProperties;

        updFunction();

        mapProperties.addObserver(Event.AREA_CHANGED, this::updFunction);
        mapProperties.addObserver(Event.COLORS_CHANGED, this::updFunction);
        mapProperties.addObserver(Event.VALUES_CHANGED, this::updFunction);
        mapProperties.addObserver(Event.FUNCTION_CHANGED, this::updFunction);
    }

    @Override
    public DoubleBinaryOperator getFunction() {
        return function;
    }

    @Override
    public void setFunction(DoubleBinaryOperator function) {
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
        Area area = mapProperties.getArea();
        double x0 = area.first.getX();
        double y0 = mapProperties.getMinValue();
        double x1 = area.second.getX();
        double y1 = mapProperties.getMaxValue();
        double k = (y1 - y0) / (x1 - x0);
        double b = y0 - k * x0;

        function = (x, y) -> x * k + b;
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
