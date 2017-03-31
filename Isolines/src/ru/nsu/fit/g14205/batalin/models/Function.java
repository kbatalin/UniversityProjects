package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public interface Function {
    double calc(int x, int y);
    double calc(Point point);
}
