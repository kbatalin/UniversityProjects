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
    private PropertiesModel applicationProperties;

    private IsolinesView isolinesView;

    public void run() {
        applicationProperties = new ApplicationProperties();
        applicationProperties.setArea(new Area(-5, -5, 5, 5));
        applicationProperties.setHorizontalCellsCount(50);
        applicationProperties.setVerticalCellsCount(50);
        applicationProperties.setGridShown(false);
        applicationProperties.setIsolinesShown(false);
        applicationProperties.setPainter(new ColorMapPainter());
        applicationProperties.setIsolinesCreatingMode(PropertiesModel.IsolinesCreatingMode.SINGLE);
        applicationProperties.setMainFunction(new SinCosFunction());
        applicationProperties.setValuesColors(new Color[]{
                new Color(255, 0, 0),
                new Color(255, 0, 255),
                new Color(0, 0, 255),
                new Color(0, 255, 255),
                new Color(0, 255, 0),
                new Color(255, 255, 0)
        });
        applicationProperties.setIsolinesColor(Color.BLUE);

        isolinesView = new IsolinesView(this);
    }

    public void onMouseMoved(MouseEvent mouseEvent) {
        Dimension mapSize = isolinesView.getWorkspaceView().getFunctionMapView().getSize();
        Point pos = mouseEvent.getPoint();
        Area area = applicationProperties.getArea();
        Dimension areaSize = area.toDimension();
        double x = pos.x / mapSize.getWidth() * areaSize.width + area.first.getX();
        double y = pos.y / mapSize.getHeight() * areaSize.height + area.first.getY();
        double f = applicationProperties.getMainFunction().applyAsDouble(x, y);
        isolinesView.getStatusBarView().setMessage(String.format("F(%.1f, %.1f) = %.1f", x, y, f));
    }

    public void onSinleIsolineButtonClicked(ActionEvent actionEvent) {
        applicationProperties.setIsolinesCreatingMode(PropertiesModel.IsolinesCreatingMode.SINGLE);
    }

    public void onSeveralIsolinesButtonClicked(ActionEvent actionEvent) {
        applicationProperties.setIsolinesCreatingMode(PropertiesModel.IsolinesCreatingMode.SEVERAL);
    }

    public void onIsolinesShowButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setIsolinesShown(button.isSelected());
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
