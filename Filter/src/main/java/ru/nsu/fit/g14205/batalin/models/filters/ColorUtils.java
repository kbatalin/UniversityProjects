package ru.nsu.fit.g14205.batalin.models.filters;

/**
 * Created by kir55rus on 29.03.17.
 */
public abstract class ColorUtils {
    public static int validateChanel(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public static int validateChanel(double value) {
        return validateChanel((int) value);
    }
}
