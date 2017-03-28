package ru.nsu.fit.g14205.batalin.models.filters;

import ru.nsu.fit.g14205.batalin.models.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class EmbossFilter extends MatrixFilter {
    public EmbossFilter() {
        super(
                new Matrix(3,3,new int[]{0,-1,0,1,0,-1,0,1,0}),
                new Point(-1,-1),
                1.,
                128.
        );
    }
}
