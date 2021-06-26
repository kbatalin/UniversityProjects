package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 29.03.17.
 */
public class ZoomFilter implements Filter{
    private BlurFilter blurFilter;

    public ZoomFilter() {
        blurFilter = new BlurFilter();
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                Color color = calcColor(srcImage, x, y);
                result.setRGB(x, y, color.getRGB());
            }
        }

        result = blurFilter.process(result);
        return result;
    }

    private Color calcColor(BufferedImage image, int x, int y) {
        int offset = 87;

        int srcX = x / 2 + offset;
        int srcY = y / 2 + offset;

        return new Color(image.getRGB(srcX, srcY));
    }
}
