package ru.nsu.fit.g14205.batalin.models;

/**
 * Created by kir55rus on 01.04.17.
 */
public class ColorUtils {
    public static float validate(double val) {
        return (float)Math.max(0., Math.min(1., val));
    }

    public static int validate(int val) {
        return Math.max(0, Math.min(255, val));
    }
}
