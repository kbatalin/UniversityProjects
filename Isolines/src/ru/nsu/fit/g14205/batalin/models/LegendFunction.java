package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public class LegendFunction implements Function {
    private PropertiesModel mapProperties;
    private Color[] colors;


    public LegendFunction(PropertiesModel mapProperties) {
        this.mapProperties = mapProperties;

        mapProperties.addObserver(PropertiesModel.Event.AREA_CHANGED, this::updFunction);
        mapProperties.addObserver(PropertiesModel.Event.FUNCTION_CHANGED, this::updFunction);
        mapProperties.addObserver(PropertiesModel.Event.COLORS_CHANGED, this::updFunction);
        mapProperties.addObserver(PropertiesModel.Event.VALUES_CHANGED, this::updFunction);

        updFunction();
    }

    private void updFunction() {
        Area area = mapProperties.getArea();
        double[] values = mapProperties.getValues();

        double len = (area.second.getX() - area.first.getX()) / values.length;

    }

    @Override
    public double calc(double x, double y) {
        return 0;
    }
}
