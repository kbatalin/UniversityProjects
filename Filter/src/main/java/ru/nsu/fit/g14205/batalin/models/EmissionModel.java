package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.Observable;
import ru.nsu.fit.g14205.batalin.utils.observe.ObservableBase;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kir55rus on 29.03.17.
 */
public class EmissionModel extends ObservableBase implements Observable {
    private ArrayList<Emission> values;

    public EmissionModel() {
        values = new ArrayList<>();
    }

    public void addValue(Emission emission) {
        values.add(emission);
        notifyObservers(EmissionModelEvent.VALUES_CHANGED);
    }

    public List<Emission> getValues() {
       return values;
    }

    public Color calc(int x) {
        int i = 0;
        for(; i < values.size(); ++i) {
            if (values.get(i).x >= x) {
                break;
            }
        }

        if (values.get(i).x == x || i == 0) {
            return values.get(i).y;
        }

        int prev = values.get(i - 1).x;
        int next = values.get(i).x;
        double ratio = (double)(x - prev) / (next - prev);
        Color color = new Color(
                (float)((values.get(i).y.getRed() - values.get(i - 1).y.getRed()) / 255. * ratio + values.get(i - 1).y.getRed() / 255.),
                (float)((values.get(i).y.getGreen() - values.get(i - 1).y.getGreen()) / 255. * ratio + values.get(i - 1).y.getGreen() / 255.),
                (float)((values.get(i).y.getBlue() - values.get(i - 1).y.getBlue()) / 255. * ratio + values.get(i - 1).y.getBlue() / 255.)
        );
        return color;
    }
}
