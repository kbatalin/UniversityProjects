package ru.nsu.fit.g14205.batalin.models.painters;

import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.FunctionProperties;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public class ColorMapPainter implements Painter {
    public Image draw(FunctionProperties functionProperties, Dimension size) {
        DoubleBinaryOperator function = functionProperties.getFunction();
        Area area = functionProperties.getArea();
        Dimension areaSize = area.toDimension();
        double widthRatio = size.getWidth() / areaSize.width;
        double heightRatio = size.getHeight() / areaSize.height;

        BufferedImage canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < size.height; ++y) {
            for(int x = 0; x < size.width; ++x) {
                double funcX =  (x / widthRatio + area.first.x);
                double funcY =  (y / heightRatio + area.first.y);
                funcX = Math.max(area.first.x, Math.min(area.second.x, funcX));
                funcY = Math.max(area.first.y, Math.min(area.second.y, funcY));

                double value = function.applyAsDouble(funcX, funcY);
                Color color = functionProperties.getValueColor(value);
                canvas.setRGB(x, y, color.getRGB());
            }
        }

        return canvas;
    }
}
