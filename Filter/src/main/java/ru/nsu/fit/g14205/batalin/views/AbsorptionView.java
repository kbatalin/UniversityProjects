package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;
import ru.nsu.fit.g14205.batalin.models.Absorption;
import ru.nsu.fit.g14205.batalin.models.AbsorptionModel;
import ru.nsu.fit.g14205.batalin.models.AbsorptionModelEvent;
import ru.nsu.fit.g14205.batalin.models.Emission;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Created by kir55rus on 29.03.17.
 */
public class AbsorptionView extends JComponent {
    private static Dimension componentDimension = new Dimension(405, 105);
    private static Dimension contentDimension = new Dimension(400, 100);
    private AbsorptionModel absorptionModel;

    public AbsorptionView(FilterController filterController) {
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.BLACK));
    }

    public void setAbsorptionModel(AbsorptionModel absorptionModel) {
        this.absorptionModel = absorptionModel;
        absorptionModel.addObserver(AbsorptionModelEvent.VALUES_CHANGED, this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (absorptionModel == null) {
            return;
        }

        java.util.List<Absorption> values = absorptionModel.getValues();

        if (values.size() == 0) {
            return;
        }

        double ratioX = contentDimension.getWidth() / 100;
        double ratioY = contentDimension.getHeight() / 1;

        Absorption prev = values.get(0);
        for (Absorption next : values) {
            graphics.drawLine(
                    (int)(prev.x * ratioX),
                    contentDimension.height - (int)(prev.y * ratioY),
                    (int)(next.x * ratioX),
                    contentDimension.height - (int)(next.y * ratioY)
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
