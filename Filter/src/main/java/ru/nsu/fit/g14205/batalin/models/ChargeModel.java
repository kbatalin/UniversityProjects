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

    public double calcChargePower(int x, int y, int z, double xSize, double ySize, double zSize) {
        double xPos = xSize * (x + 1. / 2);
        double yPos = ySize * (y + 1. / 2);
        double zPos = zSize * (z + 1. / 2);

        double power = 0.;
        for (Charge charge : charges) {
            double dist = Math.max(0.1, distance(xPos, yPos, zPos, charge.x, charge.y, charge.z));
            power += charge.power / dist;
        }

        return power;
    }

    private double distance(double x0, double y0, double z0, double x1, double y1, double z1) {
        double dx = x0 - x1;
        double dy = y0 - y1;
        double dz = z0 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
