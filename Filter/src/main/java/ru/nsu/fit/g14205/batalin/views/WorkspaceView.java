package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 11.03.17.
 */
public class WorkspaceView extends JPanel {
    private ImageView aImage;
    private ImageView bImage;
    private ImageView cImage;

    public WorkspaceView(FilterController filterController) {
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        aImage = new ImageView(filterController);
        add(aImage);

        add(Box.createHorizontalStrut(10));

        bImage = new ImageView(filterController);
        add(bImage);

        add(Box.createHorizontalStrut(10));

        cImage = new ImageView(filterController);
        add(cImage);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Rectangle area = graphics.getClip().getBounds();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(area.x, area.y, area.width, area.height);
    }
}
