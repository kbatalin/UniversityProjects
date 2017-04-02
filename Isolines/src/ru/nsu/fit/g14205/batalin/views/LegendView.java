package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 01.04.17.
 */
public class LegendView extends JComponent {
    private IsolinesController isolinesController;

    public LegendView(IsolinesController isolinesController) {
        this.isolinesController = isolinesController;

        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

        PropertiesModel properties = isolinesController.getApplicationProperties();
        properties.addObserver(PropertiesModel.Event.AREA_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.PAINTER_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.FUNCTION_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.COLORS_CHANGED, this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        PropertiesModel properties = isolinesController.getApplicationProperties();

        Dimension componentSize = graphics.getClip().getBounds().getSize();
        int fontSize = 30;
        Dimension legendSize = new Dimension(componentSize.width, Math.max(10, componentSize.height - fontSize));

        graphics.setFont(new Font("TimesRoman", Font.PLAIN, fontSize / 2));
        graphics.setColor(Color.BLACK);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int verticalOffset = fontSize * 2 / 3;
        double[] values = properties.getValues();
        double len = componentSize.getWidth() / (values.length + 1);
        for(int i = 0; i < values.length; ++i) {
            String valueStr = String.format("%.1f", values[i]);
            int horizontalOffset = (int)(len * (i + 1) - fontMetrics.stringWidth(valueStr) / 2);

            graphics.drawString(valueStr, horizontalOffset, verticalOffset);
        }

        Painter painter = properties.getPainter();
        Image legend = painter.draw(properties.getLegendFunction(), properties, legendSize);
        graphics.drawImage(legend, 0, fontSize, null);
    }

}
