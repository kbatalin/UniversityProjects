package ru.nsu.fit.g14205.batalin.models;

import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 04.04.17.
 */
public class SinCosFunction implements DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double x, double y) {
        return Math.sin(x) + Math.cos(y);
    }
}
