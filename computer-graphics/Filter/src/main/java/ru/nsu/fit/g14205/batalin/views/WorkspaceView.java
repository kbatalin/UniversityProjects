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

    private AbsorptionView absorptionView;
    private EmissionView emissionView;

    public WorkspaceView(FilterController filterController, ImageModel aImageModel, ImageModel bImageModel, ImageModel cImageModel) {
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel images = new JPanel();
        images.setBackground(Color.WHITE);
        images.setLayout(new BoxLayout(images, BoxLayout.X_AXIS));
        add(images);

        aImage = new ImageView(filterController, aImageModel);
        aImage.setAlignmentY(Component.TOP_ALIGNMENT);
        images.add(aImage);

        images.add(Box.createHorizontalStrut(10));

        bImage = new ImageView(filterController, bImageModel);
        bImage.setAlignmentY(Component.TOP_ALIGNMENT);
        images.add(bImage);

        images.add(Box.createHorizontalStrut(10));

        cImage = new ImageView(filterController, cImageModel);
        cImage.setAlignmentY(Component.TOP_ALIGNMENT);
        images.add(cImage);

        add(Box.createVerticalStrut(20));

        JPanel graphs = new JPanel();
        graphs.setBackground(Color.WHITE);
        add(graphs);

        absorptionView = new AbsorptionView(filterController);
        absorptionView.setAlignmentY(Component.TOP_ALIGNMENT);
        graphs.add(absorptionView);

        graphs.add(Box.createHorizontalStrut(40));

        emissionView = new EmissionView(filterController);
        emissionView.setAlignmentY(Component.TOP_ALIGNMENT);
        graphs.add(emissionView);
    }

    public AbsorptionView getAbsorptionView() {
        return absorptionView;
    }

    public EmissionView getEmissionView() {
        return emissionView;
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
