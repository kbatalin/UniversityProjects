package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.ColorUtils;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;

/**
 * Created by kir55rus on 01.04.17.
 */
public class GradientPainter implements Painter {
    @Override
    public Image draw(PropertiesModel propertiesModel, Dimension size) {
        DoubleBinaryOperator function = propertiesModel.getFunction();
        Area area = propertiesModel.getArea();
        Dimension areaSize = area.toDimension();
        double widthRatio = size.getWidth() / areaSize.width;
        double heightRatio = size.getHeight() / areaSize.height;
        double[] values = propertiesModel.getValues();
        double dif = values.length > 1 ? values[1] - values[0] : 0;

        BufferedImage canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < size.height; ++y) {
            for(int x = 0; x < size.width; ++x) {
                double funcX =  (x / widthRatio + area.first.x);
                double funcY =  (y / heightRatio + area.first.y);
                funcX = Math.max(area.first.x, Math.min(area.second.x, funcX));
                funcY = Math.max(area.first.y, Math.min(area.second.y, funcY));

                double value = function.applyAsDouble(funcX, funcY);
                Color color = getColor(value, propertiesModel);

                canvas.setRGB(x, y, color.getRGB());
            }
        }

        return canvas;
    }

    private Color getColor(double value, PropertiesModel propertiesModel) {
        double[] values = propertiesModel.getValues();
        Color[] colors = propertiesModel.getValuesColors();
        if (values == null || colors == null) {
            return Color.BLACK;
        }

        double len = (propertiesModel.getMaxValue() - propertiesModel.getMinValue()) / (values.length + 1);

        int i = 0;
        for(; i < values.length; ++i) {
            if (Double.compare(value, values[i] + len / 2) < 0) {
                break;
            }
        }


        double currentVal = (i > 0 ? values[i - 1] : propertiesModel.getMinValue()) + len / 2;
        double nextVal = (i < values.length ? values[i] : propertiesModel.getMaxValue()) + len / 2;

        Color color = colors[i];
        Color nextColor = colors[Math.min(colors.length - 1, i + 1)];

        double red = color.getRed() / 255. * (nextVal - value) / len + nextColor.getRed() / 255. * (value - currentVal) / len;
        double green = color.getGreen() / 255. * (nextVal - value) / len + nextColor.getGreen() / 255. * (value - currentVal) / len;
        double blue = color.getBlue() / 255. * (nextVal - value) / len + nextColor.getBlue() / 255. * (value - currentVal) / len;
//        double red = color.getRed() / 255. * (value - nextVal + len) / len + nextColor.getRed() / 255. * (nextVal - value) / len;
//        double green = color.getGreen() / 255. * (value - nextVal + len) / len + nextColor.getGreen() / 255. * (nextVal - value) / len;
//        double blue = color.getBlue() / 255. * (value - nextVal + len) / len + nextColor.getBlue() / 255. * (nextVal - value) / len;

        return new Color(
                ColorUtils.validate(red),
                ColorUtils.validate(green),
                ColorUtils.validate(blue)
        );
    }

//    private Color getNextColor(Color color, PropertiesModel propertiesModel) {
//        Color[] colors = propertiesModel.getValuesColors();
//        int i = 0;
//        for(; i < colors.length; ++i) {
//            if (color.equals(colors[i])) {
//                break;
//            }
//        }
//
//        int next = Math.min(colors.length - 1, i + 1);
//        return colors[next];
//    }
//
//    private Color getTone(Color from, Color to) {
//
//    }
}
