package ru.nsu.fit.g14205.batalin.models;

import java.util.*;

/**
 * Created by kir55rus on 29.03.17.
 */
public class AbsorptionModel {
    private ArrayList<Absorption> values;

    public AbsorptionModel() {
        values = new ArrayList<>();
    }

    public void addValue(Absorption emission) {
        values.add(emission);
    }

    public List<Absorption> getValues() {
        return values;
    }
}
