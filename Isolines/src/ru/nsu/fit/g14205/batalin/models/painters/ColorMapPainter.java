package ru.nsu.fit.g14205.batalin.models.painters;

import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public class ColorMapPainter implements Painter {
    public BufferedImage draw(DoubleBinaryOperator function, PropertiesModel properties, Dimension size) {
        Area area = properties.getArea();
        Dimension areaSize = area.toDimension();
        double widthRatio = size.getWidth() / areaSize.width;
        double heightRatio = size.getHeight() / areaSize.height;

        BufferedImage canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < size.height; ++y) {
            for(int x = 0; x < size.width; ++x) {
                double funcX =  (x / widthRatio + area.first.getX());
                double funcY =  (y / heightRatio + area.first.getY());
                funcX = Math.max(area.first.getX(), Math.min(area.second.getX(), funcX));
                funcY = Math.max(area.first.getY(), Math.min(area.second.getY(), funcY));

                double value = function.applyAsDouble(funcX, funcY);
                Color color = properties.getValueColor(value);
                canvas.setRGB(x, y, color.getRGB());
            }
        }

        return canvas;
    }
}
