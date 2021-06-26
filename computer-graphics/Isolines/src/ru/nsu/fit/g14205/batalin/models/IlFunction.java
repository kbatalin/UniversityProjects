package ru.nsu.fit.g14205.batalin.models;

import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 05.04.17.
 */
public class IlFunction implements DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double x, double y) {
        return Math.exp(-x * x - y * y / 2) * Math.cos(4 * x) + Math.exp(-3 * ((x + 0.5) * (x + 0.5) + y * y / 2));
    }
}
