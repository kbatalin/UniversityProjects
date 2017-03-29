package ru.nsu.fit.g14205.batalin.models.filters;

import ru.nsu.fit.g14205.batalin.models.Matrix;

import java.awt.*;

/**
 * Created by kir55rus on 28.03.17.
 */
public class SharpFilter extends MatrixFilter {
    public SharpFilter() {
        super(
                new Matrix(3, 3, new int[] {0,-1,0,-1,5,-1,0,-1,0}),
                new Point(-1, -1),
                1.
        );
    }
}
