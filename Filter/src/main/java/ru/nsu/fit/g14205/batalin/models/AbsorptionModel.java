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
}
