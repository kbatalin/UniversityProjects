package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;
import ru.nsu.fit.g14205.batalin.models.ImageModel;
import ru.nsu.fit.g14205.batalin.models.ImageModelEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 17.03.17.
 */
public class ImageView extends JComponent {
    private static int componentSize = 350;
    private static Dimension componentDimension = new Dimension(componentSize, componentSize);
    private static int strokeSize = 30;
    private  Point[] dirs;

    private ImageModel imageModel;
    private Rectangle selectedArea;

    public ImageView(FilterController filterController, ImageModel imageModel) {
        this.imageModel = imageModel;
        setBorder(BorderFactory.createDashedBorder(Color.BLACK));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                filterController.onMouseDragged(ImageView.this, mouseEvent);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                filterController.onMouseReleased(mouseEvent);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                filterController.onMousePressed(ImageView.this, mouseEvent);
            }
        });

        this.imageModel.addObserver(ImageModelEvent.IMAGE_UPDATED, this::repaint);

        dirs = new Point[]{
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0),
                new Point(0, -1),
        };
    }

    public void setSelectedArea(Rectangle area) {
        selectedArea = area;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        BufferedImage content = new BufferedImage(350, 350, BufferedImage.TYPE_INT_RGB);
        Graphics2D contentGraphics = content.createGraphics();
        contentGraphics.setPaint(Color.WHITE);
        contentGraphics.fillRect(0, 0, 350, 350);

        if(drawImage(contentGraphics)) {
            drawSelectedArea(content);
        }

        graphics.drawImage(content, 0, 0, 350, 350, null);
    }

    private boolean drawImage(Graphics2D contentGraphics) {
        BufferedImage image = imageModel.getImage();
        if (image == null) {
            return false;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        int maxSize = Math.max(width, height);
        if (maxSize > componentSize) {
            double ratio = (double) maxSize / componentSize;
            width = (int) (width / ratio);
            height = (int) (height / ratio);
        }

        contentGraphics.drawImage(image, 0, 0, width, height, null);
        return true;
    }

    private void drawSelectedArea(BufferedImage content) {
        if (selectedArea == null) {
            return;
        }

        Graphics2D graphics = content.createGraphics();
        graphics.setPaint(Color.BLACK);
        graphics.setXORMode(Color.WHITE);
        Stroke dotted = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {10,15}, 0);
        graphics.setStroke(dotted);
        graphics.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);
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
