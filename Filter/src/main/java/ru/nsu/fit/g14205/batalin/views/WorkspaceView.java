package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;
import ru.nsu.fit.g14205.batalin.models.ImageModel;

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

    public WorkspaceView(FilterController filterController, ImageModel aImageModel, ImageModel bImageModel, ImageModel cImageModel) {
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        aImage = new ImageView(filterController, aImageModel);
        aImage.setAlignmentY(Component.TOP_ALIGNMENT);
        add(aImage);

        add(Box.createHorizontalStrut(10));

        bImage = new ImageView(filterController, bImageModel);
        bImage.setAlignmentY(Component.TOP_ALIGNMENT);
        add(bImage);

        add(Box.createHorizontalStrut(10));

        cImage = new ImageView(filterController, cImageModel);
        cImage.setAlignmentY(Component.TOP_ALIGNMENT);
        add(cImage);
    }

    public ImageView getAImage() {
        return aImage;
    }

    public ImageView getBImage() {
        return bImage;
    }

    public ImageView getCImage() {
        return cImage;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Rectangle area = graphics.getClip().getBounds();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(area.x, area.y, area.width, area.height);
    }
}
