package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        isolinesController.getApplicationProperties().addObserver(PropertiesModel.Event.PAINTER_CHANGED, this::repaint);
        isolinesController.getApplicationProperties().addObserver(PropertiesModel.Event.GRID_SHOWN_CHANGED, this::repaint);
        isolinesController.getApplicationProperties().addObserver(PropertiesModel.Event.ISOLINES_SHOWN_CHANGED, this::repaint);
        isolinesController.getApplicationProperties().addObserver(PropertiesModel.Event.AREA_CHANGED, this::repaint);
        isolinesController.getApplicationProperties().addObserver(PropertiesModel.Event.CELLS_COUNT_CHANGED, this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Dimension mapSize = graphics.getClip().getBounds().getSize();
        Painter painter = isolinesController.getApplicationProperties().getPainter();
        Image map = painter.draw(isolinesController.getMapProperties(), mapSize);

        graphics.drawImage(map, 0, 0, null);
    }

}
