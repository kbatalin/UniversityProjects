package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.utils.observe.Observable;
import ru.nsu.fit.g14205.batalin.utils.observe.ObservableBase;


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
    }

    public List<Emission> getValues() {
       return values;
    }
}
