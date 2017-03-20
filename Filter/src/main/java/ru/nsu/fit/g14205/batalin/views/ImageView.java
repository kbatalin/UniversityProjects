package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.FilterController;
import ru.nsu.fit.g14205.batalin.models.ImageModel;
import ru.nsu.fit.g14205.batalin.models.ImageModelEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by kir55rus on 17.03.17.
 */
public class ImageView extends JComponent {
    private static int componentSize = 350;
    private static Dimension componentDimension = new Dimension(componentSize, componentSize);
    private static int strokeSize = 10;
    private  Point[] dirs;

    private ImageModel imageModel;
    private Rectangle selectedArea;

    public ImageView(FilterController filterController, ImageModel imageModel) {
        this.imageModel = imageModel;
        setBorder(BorderFactory.createDashedBorder(Color.BLACK));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                filterController.onMouseDragged(mouseEvent);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                filterController.onMouseReleased(mouseEvent);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                filterController.onMousePressed(mouseEvent);
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

        Image image = imageModel.getImage();
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

        drawSelectedArea(graphics);
    }

    private void drawSelectedArea(Graphics graphics) {
        if (selectedArea == null) {
            return;
        }

        graphics.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);

//        double ratio = 350. / selectedArea.width;
//        int stroke = Math.max(1, (int)(strokeSize / ratio));
//
//
//        int dirIndex = 0;
//        Point pos = new Point(selectedArea.x, selectedArea.y);
//        int currentStrokeSize = 0;
//        for(int i = 0, count = selectedArea.height * 2 + selectedArea.width * 2; i < count; ++i) {
//            if (currentStrokeSize >= stroke) {
//                currentStrokeSize -= 2 * stroke;
//                continue;
//            }
//        }
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
