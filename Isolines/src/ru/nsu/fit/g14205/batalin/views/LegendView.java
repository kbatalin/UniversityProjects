package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public class LegendView extends JComponent {
    private Dimension componentSize;

    public LegendView(IsolinesController isolinesController) {
        PropertiesModel propertiesModel = isolinesController.getPropertiesModel();
        double scale = propertiesModel.getScale();
        Dimension mapDimension = propertiesModel.getArea().toDimension(scale);

        componentSize = new Dimension(propertiesModel.getLegendWidth(), mapDimension.height);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, componentSize.width, componentSize.height);
    }

    @Override
    public Dimension getPreferredSize() {
        return componentSize;
    }

    @Override
    public Dimension getMaximumSize() {
        return componentSize;
    }

    @Override
    public Dimension getMinimumSize() {
        return componentSize;
    }
}
