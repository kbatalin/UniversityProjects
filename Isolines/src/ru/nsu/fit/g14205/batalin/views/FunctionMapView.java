package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.Function;
import ru.nsu.fit.g14205.batalin.models.Properties;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Created by kir55rus on 01.04.17.
 */
public class FunctionMapView extends JComponent {
    private IsolinesController isolinesController;

    public FunctionMapView(IsolinesController isolinesController) {
        this.isolinesController = isolinesController;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Rectangle clip = graphics.getClipBounds();

        PropertiesModel propertiesModel = isolinesController.getPropertiesModel();
        Function function = propertiesModel.getFunction();
        Area area = propertiesModel.getArea();
        Dimension areaSize = area.toDimension();
        double widthRatio = clip.getWidth() / areaSize.width;
        double heightRatio = clip.getHeight() / areaSize.height;

        BufferedImage map = new BufferedImage(clip.width, clip.height, BufferedImage.TYPE_INT_RGB);

        for(int y = 0; y < clip.height; ++y) {
            for(int x = 0; x < clip.width; ++x) {
                double funcX =  (x / widthRatio - areaSize.width / 2);
                double funcY =  (y / heightRatio - areaSize.height / 2);
                funcX = Math.max(area.first.x, Math.min(area.second.x, funcX));
                funcY = Math.max(area.first.y, Math.min(area.second.y, funcY));

                double value = function.calc(funcX, funcY);
                Color color = propertiesModel.getValueColor(value);
                map.setRGB(x, y, color.getRGB());
            }
        }

        graphics.drawImage(map, 0, 0, null);
    }

}
