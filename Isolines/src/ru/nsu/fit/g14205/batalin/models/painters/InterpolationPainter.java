package ru.nsu.fit.g14205.batalin.models.painters;

import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.ColorUtils;
import ru.nsu.fit.g14205.batalin.models.FunctionProperties;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public class InterpolationPainter implements Painter {
    @Override
    public BufferedImage draw(FunctionProperties functionProperties, Dimension size) {
        DoubleBinaryOperator function = functionProperties.getFunction();
        Area area = functionProperties.getArea();
        Dimension areaSize = area.toDimension();
        double widthRatio = size.getWidth() / areaSize.width;
        double heightRatio = size.getHeight() / areaSize.height;
        double[] values = functionProperties.getValues();
        double dif = values.length > 1 ? values[1] - values[0] : 0;

        BufferedImage canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < size.height; ++y) {
            for(int x = 0; x < size.width; ++x) {
                double funcX =  (x / widthRatio + area.first.getX());
                double funcY =  (y / heightRatio + area.first.getY());
                funcX = Math.max(area.first.getX(), Math.min(area.second.getX(), funcX));
                funcY = Math.max(area.first.getY(), Math.min(area.second.getY(), funcY));

                double value = function.applyAsDouble(funcX, funcY);
                Color color = getColor(value, functionProperties);

                canvas.setRGB(x, y, color.getRGB());
            }
        }

        return canvas;
    }

    private Color getColor(double value, FunctionProperties functionProperties) {
        double[] values = functionProperties.getValues();
        Color[] colors = functionProperties.getValuesColors();
        if (values == null || colors == null) {
            return Color.BLACK;
        }

        double len = (functionProperties.getMaxValue() - functionProperties.getMinValue()) / (values.length + 1);

        int i = 0;
        for(; i < values.length; ++i) {
            if (Double.compare(value, values[i] + len / 2) < 0) {
                break;
            }
        }


        double currentVal = (i > 0 ? values[i - 1] : functionProperties.getMinValue()) + len / 2;
        double nextVal = (i < values.length ? values[i] : functionProperties.getMaxValue()) + len / 2;

        Color color = colors[i];
        Color nextColor = colors[Math.min(colors.length - 1, i + 1)];

        double red = color.getRed() / 255. * (nextVal - value) / len + nextColor.getRed() / 255. * (value - currentVal) / len;
        double green = color.getGreen() / 255. * (nextVal - value) / len + nextColor.getGreen() / 255. * (value - currentVal) / len;
        double blue = color.getBlue() / 255. * (nextVal - value) / len + nextColor.getBlue() / 255. * (value - currentVal) / len;

        return new Color(
                ColorUtils.validate(red),
                ColorUtils.validate(green),
                ColorUtils.validate(blue)
        );
    }

}
