package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public class ParaboloidFunction implements Function {
    @Override
    public double calc(int x, int y) {
        return x * x + y * y;
    }

    @Override
    public double calc(Point point) {
        return calc(point.x, point.y);
    }
}
