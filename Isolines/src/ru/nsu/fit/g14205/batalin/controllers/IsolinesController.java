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
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * Created by kir55rus on 29.03.17.
 */
public class IsolinesController {
    private PropertiesModel applicationProperties;
    private int dynamicIsolineIndex = -1;

    private IsolinesView isolinesView;

    public void run() {
        applicationProperties = new ApplicationProperties();
        applicationProperties.setArea(new Area(-10, -10, 10, 10));
        applicationProperties.setHorizontalCellsCount(20);
        applicationProperties.setVerticalCellsCount(20);
        applicationProperties.setGridShown(false);
        applicationProperties.setIsolinesShown(false);
        applicationProperties.setPainter(new ColorMapPainter());
        applicationProperties.setDynamicIsolines(false);
        applicationProperties.setCreatingIsolines(false);
        applicationProperties.setMainFunction(new SinCosFunction());
        applicationProperties.setValuesColors(new Color[]{
                new Color(255, 0, 0),
                new Color(255, 0, 255),
                new Color(0, 0, 255),
                new Color(0, 255, 255),
                new Color(0, 255, 0),
                new Color(255, 255, 0)
        });
        applicationProperties.setIsolinesColor(Color.GRAY);

        isolinesView = new IsolinesView(this);
    }

    public void onMouseMoved(MouseEvent mouseEvent) {
        Point2D pos = pixel2Area(mouseEvent.getPoint());
        double f = applicationProperties.getMainFunction().applyAsDouble(pos.getX(), pos.getY());
        isolinesView.getStatusBarView().setMessage(String.format("F(%.1f, %.1f) = %.1f", pos.getX(), pos.getY(), f));
    }

    private Point2D pixel2Area(Point pos) {
        Dimension mapSize = isolinesView.getWorkspaceView().getFunctionMapView().getSize();
        Area area = applicationProperties.getArea();
        Dimension areaSize = area.toDimension();
        double x = pos.x / mapSize.getWidth() * areaSize.width + area.first.getX();
        double y = pos.y / mapSize.getHeight() * areaSize.height + area.first.getY();
        return new Point2D.Double(x, y);
    }

    public void onClearIsolinesButtonClicked(ActionEvent actionEvent) {
        applicationProperties.setIsolinesValues(new ArrayList<>());
    }

    public void onShowEntryPointsButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setEntryPointsShown(button.isSelected());
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        if (!applicationProperties.isDynamicIsolines()) {
            return;
        }

        ArrayList<Double> isolinesValues = applicationProperties.getIsolinesValues();
        Point2D pos = pixel2Area(mouseEvent.getPoint());
        double f = applicationProperties.getMainFunction().applyAsDouble(pos.getX(), pos.getY());
        if(dynamicIsolineIndex != -1) {
            isolinesValues.remove(dynamicIsolineIndex);
            dynamicIsolineIndex = -1;
        }
        isolinesValues.add(f);
        dynamicIsolineIndex = isolinesValues.size() - 1;
        dynamicIsolineIndex = isolinesValues.size() - 1;
        applicationProperties.setIsolinesValues(isolinesValues);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        if (!applicationProperties.isDynamicIsolines()) {
            return;
        }

        ArrayList<Double> isolinesValues = applicationProperties.getIsolinesValues();
        Point2D pos = pixel2Area(mouseEvent.getPoint());
        double f = applicationProperties.getMainFunction().applyAsDouble(pos.getX(), pos.getY());
        isolinesValues.add(f);
        dynamicIsolineIndex = isolinesValues.size() - 1;
        applicationProperties.setIsolinesValues(isolinesValues);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        ArrayList<Double> isolinesValues = applicationProperties.getIsolinesValues();

        if (applicationProperties.isDynamicIsolines() && dynamicIsolineIndex != -1) {
            isolinesValues.remove(dynamicIsolineIndex);
            applicationProperties.setIsolinesValues(isolinesValues);
            dynamicIsolineIndex = -1;
        }

        if (!applicationProperties.isCreatingIsolines()) {
            return;
        }

        Point2D pos = pixel2Area(mouseEvent.getPoint());
        double f = applicationProperties.getMainFunction().applyAsDouble(pos.getX(), pos.getY());
        isolinesValues.add(f);
        applicationProperties.setIsolinesValues(isolinesValues);
    }

    public void onCreateIsolineButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setCreatingIsolines(button.isSelected());
    }

    public void onDynamicIsolineButtonClicked(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof AbstractButton)) {
            return;
        }

        AbstractButton button = ((AbstractButton) sourceObj);
        applicationProperties.setDynamicIsolines(button.isSelected());
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
