package ru.nsu.fit.g14205.batalin.models.filters;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by kir55rus on 28.03.17.
 */
public class WatercolorFilter implements Filter {
    private Point[] offsets;
    private SharpFilter sharpFilter;

    public WatercolorFilter() {
        offsets = new Point[] {
                new Point(-1, -1),
                new Point(0, -1),
                new Point(1, -1),
                new Point(-1, 0),
                new Point(0, 0),
                new Point(1, 0),
                new Point(-1, 1),
                new Point(0, 1),
                new Point(1, 1),
        };

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
        if (x - 1 < 0 || x + 1 >= image.getWidth() || y - 1 < 0 || y + 1 >= image.getHeight()) {
            return new Color(0, 0, 0);
        }

        ArrayList<Integer> reds = new ArrayList<>();
        ArrayList<Integer> greens = new ArrayList<>();
        ArrayList<Integer> blues = new ArrayList<>();

        for (Point offset : offsets) {
            Color color = new Color(image.getRGB(x + offset.x, y + offset.y));
            reds.add(color.getRed());
            greens.add(color.getGreen());
            blues.add(color.getBlue());
        }

        Collections.sort(reds);
        Collections.sort(greens);
        Collections.sort(blues);

        return new Color(
                reds.get(4),
                greens.get(4),
                blues.get(4)
        );
    }
}
