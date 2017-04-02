package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.*;
import ru.nsu.fit.g14205.batalin.views.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

/**
 * Created by kir55rus on 29.03.17.
 */
public class IsolinesController {
    private FunctionProperties mapProperties;
    private FunctionProperties legendProperties;

    private Painter painter;
    private IsolinesView isolinesView;

    public void run() {
        mapProperties = new MapProperties();
        mapProperties.setFunction(new ParaboloidFunction());
        mapProperties.setArea(new Area(-5, -5, 5, 5));
        mapProperties.setValuesCount(5);
        mapProperties.setValuesColors(new Color[]{
                new Color(255, 0, 0),
                new Color(255, 0, 255),
                new Color(0, 0, 255),
                new Color(0, 255, 255),
                new Color(0, 255, 0),
                new Color(255, 255, 0)
        });

        legendProperties = new LegendProperties(mapProperties);

        painter = new ColorMapPainter();
        isolinesView = new IsolinesView(this);
    }

    public void onMouseMoved(MouseEvent mouseEvent) {
        Dimension mapSize = isolinesView.getWorkspaceView().getFunctionMapView().getSize();
        Point pos = mouseEvent.getPoint();
        Area area = mapProperties.getArea();
        Dimension areaSize = area.toDimension();
        double x = pos.x / mapSize.getWidth() * areaSize.width + area.first.x;
        double y = pos.y / mapSize.getHeight() * areaSize.height + area.first.y;
        double f = mapProperties.getFunction().applyAsDouble(x, y);
        isolinesView.getStatusBarView().setMessage(String.format("F(%.1f, %.1f) = %.1f", x, y, f));
    }

    public void onInterpolationButtonClicked(ActionEvent actionEvent) {
        painter = new InterpolationPainter();
        isolinesView.repaint();
    }

    public void onColorMapButtonClicked(ActionEvent actionEvent) {
        painter = new ColorMapPainter();
        isolinesView.repaint();
    }

    public Painter getPainter() {
        return painter;
    }

    public FunctionProperties getMapProperties() {
        return mapProperties;
    }

    public FunctionProperties getLegendProperties() {
        return legendProperties;
    }

    public void onEnterToolbarButton(MouseEvent event) {
        Component component = event.getComponent();
        if (!(component instanceof JComponent)) {
            return;
        }

        JComponent button = ((JComponent) component);

        isolinesView.getStatusBarView().setMessage(button.getToolTipText());
    }

    public void onExitToolbarButton(MouseEvent event) {
        isolinesView.getStatusBarView().setMessage("");
    }
}
