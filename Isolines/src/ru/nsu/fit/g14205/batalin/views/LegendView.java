package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public class LegendView extends JComponent {
    private IsolinesController isolinesController;

    public LegendView(IsolinesController isolinesController) {
        this.isolinesController = isolinesController;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Dimension legendSize = graphics.getClip().getBounds().getSize();
        Image legend = Painter.draw(isolinesController.getLegendProperties(), legendSize);

        graphics.drawImage(legend, 0, 0, null);
    }

}
