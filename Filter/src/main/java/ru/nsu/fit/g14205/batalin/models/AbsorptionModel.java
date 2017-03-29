package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.*;

import java.util.*;

/**
 * Created by kir55rus on 29.03.17.
 */
public class AbsorptionModel extends ObservableBase implements ru.nsu.fit.g14205.batalin.utils.observe.Observable {
    private ArrayList<Absorption> values;

    public AbsorptionModel() {
        values = new ArrayList<>();
    }

    public void addValue(Absorption emission) {
        values.add(emission);
        notifyObservers(AbsorptionModelEvent.VALUES_CHANGED);
    }

    public List<Absorption> getValues() {
        return values;
    }

    public double calc(int x) {
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
        return ratio * (values.get(i).y - values.get(i - 1).y) + values.get(i).y;
    }
}
