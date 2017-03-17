package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;

import javax.swing.*;
import java.awt.*;

/**
 * Created by kir55rus on 17.03.17.
 */
public class ImageView extends JComponent {
    private static int componentSize = 350;
    private static Dimension componentDimension = new Dimension(componentSize, componentSize);
    private Image image;

    public ImageView(FilterController filterController) {
        setBorder(BorderFactory.createDashedBorder(Color.BLACK));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (image == null) {
            return;
        }

        int width = image.getWidth(null);
        int height = image.getHeight(null);

        int maxSize = Math.max(width, height);
        if (maxSize > componentSize) {
            double ratio = (double) maxSize / componentSize;
            width = (int) (width / ratio);
            height = (int) (height / ratio);
        }

        graphics.drawImage(image, 0, 0, width, height, null);
    }

    public void setImage(Image image) {
        this.image = image;
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
