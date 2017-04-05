package ru.nsu.fit.g14205.batalin.models.painters;

import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.ColorUtils;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public class InterpolationPainter implements Painter {
    @Override
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
                Color color = getColor(value, properties);

                canvas.setRGB(x, y, color.getRGB());
            }
        }

        return canvas;
    }

    public Color getColor(double value, PropertiesModel properties) {
        double[] values = properties.getValues();
        Color[] colors = properties.getValuesColors();
        if (values == null || colors == null) {
            return Color.BLACK;
        }

        double len = (properties.getMaxValue() - properties.getMinValue()) / (values.length + 1);
        double offsetVal = Math.max(0., Math.min(properties.getMaxValue(), (value - properties.getMinValue() - len / 2)));
        double part = offsetVal / len;
        double ratio = part - (int) part;
        int prevIndex = Math.min(colors.length - 1, Math.max(0, (int)part));
        int nextIndex = Math.min(colors.length - 1, Math.max(0, prevIndex + 1));
        Color prevColor = colors[prevIndex];
        Color nextColor = colors[nextIndex];

        double red = prevColor.getRed()/255. + (nextColor.getRed() - prevColor.getRed()) / 255. * ratio;
        double green = prevColor.getGreen()/255. + (nextColor.getGreen() - prevColor.getGreen()) / 255. * ratio;
        double blue = prevColor.getBlue()/255. + (nextColor.getBlue() - prevColor.getBlue()) / 255. * ratio;

        return new Color(
                ColorUtils.validate(red),
                ColorUtils.validate(green),
                ColorUtils.validate(blue)
        );
    }

}
