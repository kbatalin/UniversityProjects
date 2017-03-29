package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class OrderedDitherFilter implements Filter {
    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());

        int n = 16;
        int[] matrix = genMatrix(n);

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                int i = x % n;
                int j = y % n;

                Color oldColor = new Color(srcImage.getRGB(x, y));
                Color newColor = new Color(
                        oldColor.getRed() > matrix[j * n + i] ? 255 : 0,
                        oldColor.getGreen() > matrix[j * n + i] ? 255 : 0,
                        oldColor.getBlue() > matrix[j * n + i] ? 255 : 0
                );

                result.setRGB(x, y, newColor.getRGB());
            }
        }

        return result;
    }

    private int[] genMatrix(int size) {
        if(size == 2) {
            return new int[] {
              3, 1, 0, 2
            };
        }

        int subMatrixSize = size / 2;
        int[] subMatrix = genMatrix(subMatrixSize);

        int[] matrix = new int[size * size];
        for(int y = 0; y < size; ++y) {
            for(int x = 0; x < size; ++x) {
                int subMatrixX = x % subMatrixSize;
                int subMatrixY = y % subMatrixSize;
                int val = 4 * subMatrix[subMatrixY * subMatrixSize + subMatrixX];
                if(y < subMatrixSize && x < subMatrixSize) {
                    val += 3;
                } else if(y < subMatrixSize) {
                    val += 1;
                } else if(x >= subMatrixSize) {
                    val += 2;
                }

                matrix[y * size + x] = val;
            }
        }

        return matrix;
    }
}
