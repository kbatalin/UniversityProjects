package ru.nsu.fit.g14205.batalin.models.filters;

import ru.nsu.fit.g14205.batalin.models.ImageModel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 22.03.17.
 */
public class BlackWhiteFilter implements Filter {
    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage image = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());

        for(int y = 0; y < srcImage.getHeight(); ++y) {
            for (int x = 0; x < srcImage.getWidth(); ++x) {
                Color color = new Color(srcImage.getRGB(x, y));
                int Y = (int)((0.299 * color.getRed()) + (0.587 * color.getGreen()) + (0.114 * color.getBlue()));
                Color newColor = new Color(Y, Y, Y);
                image.setRGB(x, y, newColor.getRGB());
            }
        }

        return image;
    }
}
