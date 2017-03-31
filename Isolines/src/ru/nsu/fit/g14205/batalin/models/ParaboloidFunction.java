package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public class ParaboloidFunction implements Function {
    @Override
    public double calc(double x, double y) {
        return x * x + y * y;
    }
}
