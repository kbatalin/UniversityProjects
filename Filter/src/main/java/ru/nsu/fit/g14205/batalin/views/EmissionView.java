package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;
import ru.nsu.fit.g14205.batalin.models.Emission;
import ru.nsu.fit.g14205.batalin.models.EmissionModel;
import ru.nsu.fit.g14205.batalin.models.EmissionModelEvent;

import java.util.List;
import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 29.03.17.
 */
public class EmissionView extends JComponent {
    private static Dimension componentDimension = new Dimension(405, 105);
    private static Dimension contentDimension = new Dimension(400, 100);
    private EmissionModel emissionModel;

    public EmissionView(FilterController filterController) {
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.BLACK));
    }

    public void setEmissionModel(EmissionModel emissionModel) {
        this.emissionModel = emissionModel;
        emissionModel.addObserver(EmissionModelEvent.VALUES_CHANGED, this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (emissionModel == null) {
            return;
        }

        java.util.List<Emission> values = emissionModel.getValues();

        if (values.size() == 0) {
            return;
        }

        double ratioX = contentDimension.getWidth() / 100;
        double ratioY = contentDimension.getHeight() / 255;

        Emission prev = values.get(0);
        for (Emission next : values) {
            graphics.setColor(Color.RED);
            graphics.drawLine(
                    (int)(prev.x * ratioX),
                    contentDimension.height - (int)(prev.y.getRed() * ratioY),
                    (int)(next.x * ratioX),
                    contentDimension.height - (int)(next.y.getRed() * ratioY)
            );

            graphics.setColor(Color.GREEN);
            graphics.drawLine(
                    (int)(prev.x * ratioX),
                    contentDimension.height - (int)(prev.y.getGreen() * ratioY) + 1,
                    (int)(next.x * ratioX),
                    contentDimension.height - (int)(next.y.getGreen() * ratioY) + 1
            );

            graphics.setColor(Color.BLUE);
            graphics.drawLine(
                    (int)(prev.x * ratioX),
                    contentDimension.height - (int)(prev.y.getBlue() * ratioY) + 2,
                    (int)(next.x * ratioX),
                    contentDimension.height - (int)(next.y.getBlue() * ratioY) + 2
            );
            prev = next;
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return componentDimension;
    }

    @Override
    public Dimension getMaximumSize() {
        return componentDimension;
    }

    @Override
    public Dimension getMinimumSize() {
        return componentDimension;
    }
}
