package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public class ParaboloidFunction implements DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double x, double y) {
//        double res = (x - 3) * (x - 3) + (y + 1) * (y + 1);
//        double mod = 10.;
//        res = res - (int)(res / mod) * mod;
//        return res;
        return x * x + y * y;
    }
}
