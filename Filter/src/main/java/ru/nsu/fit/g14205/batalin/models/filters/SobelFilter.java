package ru.nsu.fit.g14205.batalin.models.filters;

import ru.nsu.fit.g14205.batalin.models.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class SobelFilter implements Filter {
    private int level;
    private BlackWhiteFilter blackWhiteFilter;
    private Matrix matrixX;
    private Matrix matrixY;

    public SobelFilter(int level) {
        this.level = level;
        blackWhiteFilter = new BlackWhiteFilter();

        matrixX = new Matrix(3, 3, new int[] {-1, 0, 1, -2, 0, 2, -1, 0, 1});
        matrixY = new Matrix(3, 3, new int[] {-1, -2, -1, 0, 0, 0, 1, 2, 1});
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage bwImage = blackWhiteFilter.process(srcImage);
        BufferedImage result = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                Color newColor = calcColor(bwImage, x, y);
                result.setRGB(x, y, newColor.getRGB());
            }
        }

        return result;
    }

    private Color calcColor(BufferedImage image, int x, int y) {
        if (x - 1 < 0 || x + 1 >= image.getWidth() || y - 1 < 0 || y + 1 >= image.getHeight()) {
            return new Color(0, 0, 0);
        }

        int[] sumX = matrixX.convolution(image, x - 1, y - 1);
        int[] sumY = matrixY.convolution(image, x - 1, y - 1);

        int red = Math.abs(sumX[0]) + Math.abs(sumY[0]);
        int green = Math.abs(sumX[1]) + Math.abs(sumY[1]);
        int blue = Math.abs(sumX[2]) + Math.abs(sumY[2]);

        return new Color(
                red > level ? 255 : 0,
                green > level ? 255 : 0,
                blue > level ? 255 : 0
        );
    }
}
