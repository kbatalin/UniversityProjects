package ru.nsu.fit.g14205.batalin.models;

import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 04.04.17.
 */
public class SinCosFunction implements DoubleBinaryOperator {
    @Override
    public double applyAsDouble(double x, double y) {
        //х*cos(ф) -у*sin(ф); х*sin(ф) +у*cos(ф))
        double newX = x * Math.cos(Math.PI / 4) - y * Math.sin(Math.PI / 4);
        double newY = x * Math.sin(Math.PI / 4) + y * Math.cos(Math.PI / 4);
        return Math.sin(newX) + Math.cos(newY);
    }
}
