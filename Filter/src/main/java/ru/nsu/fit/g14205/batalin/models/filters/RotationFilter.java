package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class RotationFilter implements Filter {
    private int angle;

    public RotationFilter(int angle) {
        this.angle = angle;
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());

        double radians = angle * Math.PI / 180.;
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        Point center = new Point(result.getWidth() / 2, result.getHeight() / 2);

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                int srcX = center.x + (int) ((x - center.x) * cos + (y - center.y) * sin);
                int srcY = center.y + (int) (-(x - center.x) * sin + (y - center.y) * cos);

                if (srcX < 0 || srcX >= srcImage.getWidth() || srcY < 0 || srcY >= srcImage.getHeight()) {
                    result.setRGB(x, y, Color.WHITE.getRGB());
                    continue;
                }

                int color = srcImage.getRGB(srcX, srcY);
                result.setRGB(x, y, color);
            }
        }

        return result;
    }
}
