package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;
import ru.nsu.fit.g14205.batalin.models.AbsorptionModel;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 29.03.17.
 */
public class AbsorptionView extends JComponent {
    private static Dimension componentDimension = new Dimension(400, 100);

    public AbsorptionView(FilterController filterController, AbsorptionModel absorptionModel) {
        setBorder(BorderFactory.createMatteBorder(0, 1, 1, 0, Color.BLACK));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);


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
