package ru.nsu.fit.g14205.batalin.models.filters;

import ru.nsu.fit.g14205.batalin.models.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class MatrixFilter implements Filter {
    private Matrix matrix;
    private Point matrixOffset;
    private double multiplier;
    private double addendum;

    public MatrixFilter(Matrix matrix, Point matrixOffset) {
        this(matrix, matrixOffset, 1.);
    }

    public MatrixFilter(Matrix matrix, Point matrixOffset, double multiplier) {
        this(matrix, matrixOffset, multiplier, 0.);
    }

    public MatrixFilter(Matrix matrix, Point matrixOffset, double multiplier, double addendum) {
        this.matrix = matrix;
        this.matrixOffset = matrixOffset;
        this.multiplier = multiplier;
        this.addendum = addendum;
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                Color newColor = calcColor(srcImage, x, y);
                result.setRGB(x, y, newColor.getRGB());
            }
        }

        return result;
    }

    private Color calcColor(BufferedImage image, int x, int y) {
        int matrixStartX = x + matrixOffset.x;
        int matrixStartY = y + matrixOffset.y;
        int matrixEndX = matrixStartX + matrix.getWidth();
        int matrixEndY = matrixStartY + matrix.getHeight();

        if (matrixStartX < 0 || matrixEndX >= image.getWidth() || matrixStartY < 0 || matrixEndY >= image.getHeight()) {
            return new Color(0, 0, 0);
        }

        int[] res = matrix.convolution(image, matrixStartX, matrixStartY);

        return new Color(
                validateChanel((int) (multiplier * res[0] + addendum)),
                validateChanel((int) (multiplier * res[1] + addendum)),
                validateChanel((int) (multiplier * res[2] + addendum))
        );
    }

    private int validateChanel(int value) {
        return Math.max(0, Math.min(255, value));
    }
}
