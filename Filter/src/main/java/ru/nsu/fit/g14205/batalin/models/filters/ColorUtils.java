package ru.nsu.fit.g14205.batalin.models.filters;

/**
 * Created by kir55rus on 29.03.17.
 */
public abstract class ColorUtils {
    public static int validate(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public static float validate(double value) {
        return (float)Math.max(0., Math.min(1., value));
    }
}
