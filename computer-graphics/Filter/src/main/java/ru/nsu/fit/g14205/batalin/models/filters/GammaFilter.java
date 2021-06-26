package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 29.03.17.
 */
public class GammaFilter implements Filter {
    private double gamma;

    public GammaFilter(double gamma) {
        this.gamma = gamma;
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                Color color = new Color(srcImage.getRGB(x, y));
                Color newColor = new Color(
                        (float)Math.pow(color.getRed() / 255., gamma),
                        (float) Math.pow(color.getGreen() / 255., gamma),
                        (float)Math.pow(color.getBlue() / 255., gamma)
                );

                result.setRGB(x, y, newColor.getRGB());
            }
        }

        return result;
    }
}
