package ru.nsu.fit.g14205.batalin.models.filters;

import ru.nsu.fit.g14205.batalin.models.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class BlurFilter extends MatrixFilter {
    public BlurFilter() {
        super(
                new Matrix(3, 3, new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1}),
                new Point(-1, -1),
                1. / 9
        );

    }
}
