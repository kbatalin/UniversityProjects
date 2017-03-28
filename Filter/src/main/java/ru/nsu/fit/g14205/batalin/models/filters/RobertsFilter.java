package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class RobertsFilter implements Filter{
    private int level;
    private Filter blackWhiteFilter;

    public RobertsFilter(int level) {
        this.level = level;
        blackWhiteFilter = new BlackWhiteFilter();
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = blackWhiteFilter.process(srcImage);

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                Color newColor = F(result, x, y);
                result.setRGB(x, y, newColor.getRGB());
            }
        }

        return result;
    }

    private Color F(BufferedImage image, int x, int y) {
        if (x < 0 || x + 1 >= image.getWidth() || y < 0 || y + 1 >= image.getHeight()) {
            return new Color(0, 0, 0);
        }

        Color color1 = new Color(image.getRGB(x, y));
        Color color2 = new Color(image.getRGB(x + 1, y + 1));
        Color color3 = new Color(image.getRGB(x + 1, y));
        Color color4 = new Color(image.getRGB(x, y + 1));

        int red = Math.abs(color1.getRed() - color2.getRed()) + Math.abs(color3.getRed() - color4.getRed());
        int green = Math.abs(color1.getGreen() - color2.getGreen()) + Math.abs(color3.getGreen() - color4.getGreen());
        int blue = Math.abs(color1.getBlue() - color2.getBlue()) + Math.abs(color3.getBlue() - color4.getBlue());
        return new Color(
                red > level ? 255 : 0,
                green > level ? 255 : 0,
                blue > level ? 255 : 0
        );
    }
}
