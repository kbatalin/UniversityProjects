package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kir55rus on 28.03.17.
 */
public class WatercolorFilter implements Filter {
    private int filterSize = 5;
    private SharpFilter sharpFilter;

    public WatercolorFilter() {
        sharpFilter = new SharpFilter();
    }

    @Override
    public BufferedImage process(BufferedImage srcImage) {
        BufferedImage result = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());

        for(int y = 0; y < result.getHeight(); ++y) {
            for(int x = 0; x < result.getWidth(); ++x) {
                Color newColor = calcColor(srcImage, x, y);
                result.setRGB(x, y, newColor.getRGB());
            }
        }

        result = sharpFilter.process(result);

        return result;
    }

    private Color calcColor(BufferedImage image, int x, int y) {
        int halfSize = filterSize / 2;
        if (x - halfSize < 0 || x + halfSize >= image.getWidth() || y - halfSize < 0 || y + halfSize >= image.getHeight()) {
            return new Color(0, 0, 0);
        }

        ArrayList<Integer> reds = new ArrayList<>();
        ArrayList<Integer> greens = new ArrayList<>();
        ArrayList<Integer> blues = new ArrayList<>();

        for(int i = -halfSize; i <= halfSize; ++i) {
            for(int j = -halfSize; j <= halfSize; ++j) {
                Color color = new Color(image.getRGB(x + i, y + j));
                reds.add(color.getRed());
                greens.add(color.getGreen());
                blues.add(color.getBlue());
            }
        }

        Collections.sort(reds);
        Collections.sort(greens);
        Collections.sort(blues);

        int middle = filterSize * filterSize / 2;
        return new Color(
                reds.get(middle),
                greens.get(middle),
                blues.get(middle)
        );
    }
}
