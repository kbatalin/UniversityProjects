package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 17.03.17.
 */
public class ImageView extends JComponent {
    private static Dimension size = new Dimension(350, 350);
    private Image image;

    public ImageView(FilterController filterController) {
        setBorder(BorderFactory.createDashedBorder(Color.BLACK));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        System.out.println(1);
    }

    @Override
    protected void paintBorder(Graphics graphics) {
        super.paintBorder(graphics);

        System.out.println(2);
    }

    public void setImage(Image image) {
        this.image = image;
    }

    @Override
    public Dimension getPreferredSize() {
        return size;
    }

    @Override
    public Dimension getMaximumSize() {
        return size;
    }

    @Override
    public Dimension getMinimumSize() {
        return size;
    }
}
