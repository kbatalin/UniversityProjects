package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 22.03.17.
 */
public class NegativeFilter implements Filter {
    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage image = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());

        for(int y = 0; y < srcImage.getHeight(); ++y) {
            for (int x = 0; x < srcImage.getWidth(); ++x) {
                Color color = new Color(srcImage.getRGB(x, y));
                Color newColor = new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
                image.setRGB(x, y, newColor.getRGB());
            }
        }

        return image;
    }
}
