package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 27.03.17.
 */
public class FloydSteinbergFilter implements Filter {
    private int red;
    private int green;
    private int blue;

    public FloydSteinbergFilter(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = new BufferedImage(
                srcImage.getColorModel(),
                srcImage.copyData(srcImage.getRaster().createCompatibleWritableRaster()),
                srcImage.isAlphaPremultiplied(),
                null
        );

        for(int y = 0; y < srcImage.getHeight(); ++y) {
            for(int x = 0; x < srcImage.getWidth(); ++x) {
                Color input = new Color(result.getRGB(x, y));
                Color output = new Color(
                        getNearest(input.getRed(), red),
                        getNearest(input.getGreen(), green),
                        getNearest(input.getBlue(), blue)
                );
                result.setRGB(x, y, output.getRGB());

                int redError = input.getRed() - output.getRed();
                int greenError = input.getGreen() - output.getGreen();
                int blueError = input.getBlue() - output.getBlue();

                addError(result, x + 1, y, (int)(7./16*redError), (int)(7./16*greenError), (int)(7./16*blueError));
                addError(result, x - 1, y + 1, (int)(3./16*redError), (int)(3./16*greenError), (int)(3./16*blueError));
                addError(result, x, y + 1, (int)(5./16*redError), (int)(5./16*greenError), (int)(5./16*blueError));
                addError(result, x + 1, y + 1, (int)(1./16*redError), (int)(1./16*greenError), (int)(1./16*blueError));
            }
        }

        return result;
    }

    private static int getNearest(int color, int colorCount) {
        double intervalSize = 255. / (colorCount - 1);
        double size = 256. / colorCount;
        int toneNumber = (int) (color / size);
        return (int) (toneNumber * intervalSize);
    }

    private void addError(BufferedImage image, int x, int y, int redError, int greenError, int blueError) {
        if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
            return;
        }

        Color oldColor = new Color(image.getRGB(x, y));
        Color newColor = new Color(
                validateChanel(oldColor.getRed() + redError),
                validateChanel(oldColor.getGreen() + greenError),
                validateChanel(oldColor.getBlue() + blueError)
        );
        image.setRGB(x, y, newColor.getRGB());
    }

    private int validateChanel(int value) {
        return Math.min(255, Math.max(0, value));
    }
}
