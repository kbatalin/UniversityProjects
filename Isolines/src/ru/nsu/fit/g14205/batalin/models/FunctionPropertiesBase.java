package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.ObservableBase;

import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public abstract class FunctionPropertiesBase extends ObservableBase implements FunctionProperties {
    @Override
    public Color getValueColor(double value) {
        double[] values = getValues();
        Color[] colors = getValuesColors();
        if (values == null || colors == null) {
            return Color.BLACK;
        }

        for(int i = 0; i < values.length; ++i) {
            if (Double.compare(value, values[i]) < 0) {
                return colors[i];
            }
        }

        return colors[colors.length - 1];

    }
}
