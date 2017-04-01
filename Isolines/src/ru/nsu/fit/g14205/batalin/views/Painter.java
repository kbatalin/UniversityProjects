package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.Function;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 01.04.17.
 */
public class Painter {
    public static Image draw(PropertiesModel propertiesModel, Dimension size) {
        Function function = propertiesModel.getFunction();
        Area area = propertiesModel.getArea();
        Dimension areaSize = area.toDimension();
        double widthRatio = size.getWidth() / areaSize.width;
        double heightRatio = size.getHeight() / areaSize.height;

        BufferedImage canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < size.height; ++y) {
            for(int x = 0; x < size.width; ++x) {
                double funcX =  (x / widthRatio - areaSize.width / 2);
                double funcY =  (y / heightRatio - areaSize.height / 2);
                funcX = Math.max(area.first.x, Math.min(area.second.x, funcX));
                funcY = Math.max(area.first.y, Math.min(area.second.y, funcY));

                double value = function.calc(funcX, funcY);
                Color color = propertiesModel.getValueColor(value);
                canvas.setRGB(x, y, color.getRGB());
            }
        }

        return canvas;
    }
}
