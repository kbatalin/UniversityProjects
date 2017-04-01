package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;

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
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Dimension componentSize = graphics.getClip().getBounds().getSize();
        int fontSize = 30;
        Dimension legendSize = new Dimension(componentSize.width, Math.max(10, componentSize.height - fontSize));

        graphics.setFont(new Font("TimesRoman", Font.PLAIN, fontSize / 2));
        graphics.setColor(Color.BLACK);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int verticalOffset = fontSize * 2 / 3;
        double[] values = isolinesController.getLegendProperties().getValues();
        double len = componentSize.getWidth() / (values.length + 1);
        for(int i = 0; i < values.length; ++i) {
            String valueStr = String.format("%.1f", values[i]);
            int horizontalOffset = (int)(len * (i + 1) - fontMetrics.stringWidth(valueStr) / 2);

            graphics.drawString(valueStr, horizontalOffset, verticalOffset);
        }


        Image legend = isolinesController.getPainter().draw(isolinesController.getLegendProperties(), legendSize);
        graphics.drawImage(legend, 0, fontSize, null);
    }

}
