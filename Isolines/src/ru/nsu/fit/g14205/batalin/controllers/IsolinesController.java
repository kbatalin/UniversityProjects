package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.models.painters.ColorMapPainter;
import ru.nsu.fit.g14205.batalin.models.painters.InterpolationPainter;
import ru.nsu.fit.g14205.batalin.views.*;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

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
    private PropertiesModel applicationProperties;

    private IsolinesView isolinesView;

    public void run() {
        applicationProperties = new ApplicationProperties();
        applicationProperties.setArea(new Area(-5, -5, 5, 5));
        applicationProperties.setHorizontalCellsCount(5);
        applicationProperties.setVerticalCellsCount(5);
        applicationProperties.setGridShown(false);
        applicationProperties.setIsolinesShown(true);
        applicationProperties.setPainter(new ColorMapPainter());

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

    public void onGridButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setGridShown(button.isSelected());
    }

    public void onInterpolationButtonClicked(ActionEvent actionEvent) {
        applicationProperties.setPainter(new InterpolationPainter());
    }

    public void onColorMapButtonClicked(ActionEvent actionEvent) {
        applicationProperties.setPainter(new ColorMapPainter());
    }

    public PropertiesModel getApplicationProperties() {
        return applicationProperties;
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
