package ru.nsu.fit.g14205.batalin.views;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 11.03.17.
 */
public class WorkspaceView extends JPanel {
    public WorkspaceView() {

    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Shape clip = graphics.getClip();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, clip.getBounds().width, clip.getBounds().height);
    }
}
