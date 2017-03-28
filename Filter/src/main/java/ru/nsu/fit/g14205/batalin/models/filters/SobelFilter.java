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

    public SobelFilter(int level) {
        this.level = level;
        blackWhiteFilter = new BlackWhiteFilter();
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = blackWhiteFilter.process(srcImage);

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                Color newColor = S(result, x, y);
                result.setRGB(x, y, newColor.getRGB());
            }
        }

        return result;
    }

    private Color S(BufferedImage image, int x, int y) {
        if (x <= 0 || x + 1 >= image.getWidth() || y <= 0 || y + 1 >= image.getHeight()) {
            return new Color(0, 0, 0);
        }

        Color a = new Color(image.getRGB(x - 1, y - 1));
        Color b = new Color(image.getRGB(x, y - 1));
        Color c = new Color(image.getRGB(x + 1, y - 1));
        Color d = new Color(image.getRGB(x - 1, y));
        Color f = new Color(image.getRGB(x + 1, y));
        Color g = new Color(image.getRGB(x - 1, y + 1));
        Color h = new Color(image.getRGB(x, y + 1));
        Color i = new Color(image.getRGB(x + 1, y + 1));

        int[] sX = Sx(a, c, d, f, g, i);
        int[] sY = Sy(a, b, c, g, h, i);

        int red = Math.abs(sX[0]) + Math.abs(sY[0]);
        int green = Math.abs(sX[1]) + Math.abs(sY[1]);
        int blue = Math.abs(sX[2]) + Math.abs(sY[2]);
        return new Color(
                red > level ? 255 : 0,
                green > level ? 255 : 0,
                blue > level ? 255 : 0
        );
    }

    private int[] Sx(Color a, Color c, Color d, Color f, Color g, Color i) {
        int red = Math.abs(c.getRed() + 2 * f.getRed() + i.getRed()) - Math.abs(a.getRed() + 2 * d.getRed() + g.getRed());
        int green = Math.abs(c.getGreen() + 2 * f.getGreen() + i.getGreen()) - Math.abs(a.getGreen() + 2 * d.getGreen() + g.getGreen());
        int blue = Math.abs(c.getBlue() + 2 * f.getBlue() + i.getBlue()) - Math.abs(a.getBlue() + 2 * d.getBlue() + g.getBlue());

        return new int[]{red, green, blue};
    }

    private int[] Sy(Color a, Color b, Color c, Color g, Color h, Color i) {
        int red = Math.abs(g.getRed() + 2 * h.getRed() + i.getRed()) - Math.abs(a.getRed() + 2 * b.getRed() + c.getRed());
        int green = Math.abs(g.getGreen() + 2 * h.getGreen() + i.getGreen()) - Math.abs(a.getGreen() + 2 * b.getGreen() + c.getGreen());
        int blue = Math.abs(g.getBlue() + 2 * h.getBlue() + i.getBlue()) - Math.abs(a.getBlue() + 2 * b.getBlue() + c.getBlue());

        return new int[]{red, green, blue};
    }
}
