package ru.nsu.fit.g14205.batalin.models;

import java.util.*;

/**
 * Created by kir55rus on 29.03.17.
 */
public class ChargeModel {
    private ArrayList<Charge> charges;

    public ChargeModel() {
        charges = new ArrayList<>();
    }

    public void addCharge(Charge charge) {
        charges.add(charge);
    }

    public List<Charge> getValues() {
        return charges;
    }
}
