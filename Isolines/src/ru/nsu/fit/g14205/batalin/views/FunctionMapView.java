package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 01.04.17.
 */
public class FunctionMapView extends JComponent {
    private IsolinesController isolinesController;

    public FunctionMapView(IsolinesController isolinesController) {
        this.isolinesController = isolinesController;

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                isolinesController.onMouseMoved(mouseEvent);
            }
        });

        PropertiesModel properties = isolinesController.getApplicationProperties();
        properties.addObserver(PropertiesModel.Event.CELLS_COUNT_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.AREA_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.PAINTER_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.ISOLINES_SHOWN_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.GRID_SHOWN_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.FUNCTION_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.COLORS_CHANGED, this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        PropertiesModel properties = isolinesController.getApplicationProperties();
        Dimension mapSize = graphics.getClip().getBounds().getSize();
        Painter painter = properties.getPainter();
        BufferedImage map = painter.draw(properties.getMainFunction(), properties, mapSize);

        if (isolinesController.getApplicationProperties().isGridShown()) {
            paintGrid(map);
        }

        graphics.drawImage(map, 0, 0, null);
    }

    private void paintGrid(BufferedImage map) {
        PropertiesModel applicationProperties = isolinesController.getApplicationProperties();

        Graphics2D graphics = map.createGraphics();
        graphics.setPaint(Color.BLACK);
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        graphics.setStroke(dashed);

        for(int i = 1, count = applicationProperties.getHorizontalCellsCount(), delta = map.getHeight() / count; i < count; ++i) {
            int crd = i * delta;
            graphics.drawLine(0, crd, map.getWidth() - 1, crd);
        }

        for(int i = 1, count = applicationProperties.getVerticalCellsCount(), delta = map.getWidth() / count; i < count; ++i) {
            int crd = i * delta;
            graphics.drawLine(crd, 0, crd, map.getWidth() - 1);
        }
    }
}
