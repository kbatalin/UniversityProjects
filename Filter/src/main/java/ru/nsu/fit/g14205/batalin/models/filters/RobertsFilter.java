package ru.nsu.fit.g14205.batalin.models.filters;

import ru.nsu.fit.g14205.batalin.models.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class RobertsFilter implements Filter{
    private int level;
    private Filter blackWhiteFilter;
    private Matrix matrix1;
    private Matrix matrix2;

    public RobertsFilter(int level) {
        this.level = level;
        blackWhiteFilter = new BlackWhiteFilter();

        matrix1 = new Matrix(2, 2, new int[] {1, 0, 0, -1});
        matrix2 = new Matrix(2, 2, new int[] {0, 1, -1, 0});
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = blackWhiteFilter.process(srcImage);

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                Color newColor = calcColor(result, x, y);
                result.setRGB(x, y, newColor.getRGB());
            }
        }

        return result;
    }

    private Color calcColor(BufferedImage image, int x, int y) {
        if (x < 0 || x + 1 >= image.getWidth() || y < 0 || y + 1 >= image.getHeight()) {
            return new Color(0,0,0);
        }

        int[] sum1 = matrix1.convolution(image, x, y);
        int[] sum2 = matrix2.convolution(image, x, y);

        int red = Math.abs(sum1[0]) + Math.abs(sum2[0]);
        int green = Math.abs(sum1[1]) + Math.abs(sum2[1]);
        int blue = Math.abs(sum1[2]) + Math.abs(sum2[2]);

        return new Color(
                red > level ? 255 : 0,
                green > level ? 255 : 0,
                blue > level ? 255 : 0
        );

    }
}
