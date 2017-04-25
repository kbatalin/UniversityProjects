package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.EditorDialog;
import ru.nsu.fit.g14205.batalin.views.LineEditorContentView;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * Created by kir55rus on 12.04.17.
 */
public class EditorController {
    private WireframeController wireframeController;
    private ApplicationProperties applicationProperties;
    private EditorModel editorModel;

    private EditorDialog dialog;

    private int activeControlPoint = -1;

    public EditorController(WireframeController wireframeController) {
        this.wireframeController = wireframeController;
        try {
            this.applicationProperties = wireframeController.getApplicationProperties().clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        this.editorModel = new EditorModel(applicationProperties);
    }

    public void run() {
        dialog = new EditorDialog(this);
        dialog.pack();
        dialog.setLocationRelativeTo(wireframeController.getWireframeView());
        dialog.setVisible(true);

        if (!dialog.getResult()) {
            return;
        }

        wireframeController.getApplicationProperties().apply(applicationProperties);
    }

    public void onApplyButtonClicked() {
        try {
            wireframeController.getApplicationProperties().apply(applicationProperties.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public void onAddButtonClicked() {
        LineProperties lineProperties = new BSplineProperties(applicationProperties);
        FigureProperties figureProperties = new FigurePropertiesDefault(lineProperties);
        applicationProperties.addFigureProperties(figureProperties);
        editorModel.setCurrentFigure(applicationProperties.getFigurePropertiesCount() - 1);
    }

    public void onDeleteButtonClicked() {
        int currentLine = editorModel.getCurrentFigure();
        --currentLine;
        if (currentLine >= 0) {
            editorModel.setCurrentFigure(currentLine);
            applicationProperties.delFigureProperties(currentLine + 1);
            return;
        }

        if (applicationProperties.getFigurePropertiesCount() > 1) {
            applicationProperties.delFigureProperties(0);
            editorModel.setCurrentFigure(0);
            return;
        }

        applicationProperties.delFigureProperties(0);
        LineProperties lineProperties = new BSplineProperties(applicationProperties);
        FigureProperties figureProperties = new FigurePropertiesDefault(lineProperties);
        applicationProperties.addFigureProperties(figureProperties);
        editorModel.setCurrentFigure(0);
    }

    public void onNSpinnerChanged(int value) {
        applicationProperties.getGrid().setCols(value);
    }

    public void onMSpinnerChanged(int value) {
        applicationProperties.getGrid().setRows(value);
    }

    public void onKSpinnerChanged(int value) {
        applicationProperties.getGrid().setSegmentSplitting(value);
    }

    public void onNumberSpinnerChanged(int value) {
        editorModel.setCurrentFigure(value);
    }

    public void onRedSpinnerChanged(int value) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        LineProperties lineProperties = figureProperties.getLineProperties();
        Color color = lineProperties.getColor();
        lineProperties.setColor(new Color(value, color.getGreen(), color.getBlue()));
    }

    public void onGreenSpinnerChanged(int value) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        LineProperties lineProperties = figureProperties.getLineProperties();
        Color color = lineProperties.getColor();
        lineProperties.setColor(new Color(color.getRed(), value, color.getBlue()));
    }

    public void onBlueSpinnerChanged(int value) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        LineProperties lineProperties = figureProperties.getLineProperties();
        Color color = lineProperties.getColor();
        lineProperties.setColor(new Color(color.getRed(), color.getGreen(), value));
    }

    public void onZoomSliderChanged(int value) {
        editorModel.setZoom(value);
    }

    public EditorModel getEditorModel() {
        return editorModel;
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        LineProperties lineProperties = figureProperties.getLineProperties();
        Point2D pos = pixel2Point(mouseEvent.getPoint());
        int controlPointIndex = lineProperties.getControlPointId(pos);
        if (controlPointIndex == -1) {
            lineProperties.addControlPoint(pos);
            activeControlPoint = lineProperties.getControlPointsCount() - 1;
            return;
        }

        if(mouseEvent.getButton() == MouseEvent.BUTTON1) {
            activeControlPoint = controlPointIndex;
        } else if(mouseEvent.getButton() == MouseEvent.BUTTON3) {
            activeControlPoint = -1;
            lineProperties.delControlPoint(controlPointIndex);
        }
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        if (activeControlPoint == -1) {
            return;
        }

        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        LineProperties lineProperties = figureProperties.getLineProperties();
        Point2D pos = pixel2Point(mouseEvent.getPoint());
        lineProperties.setControlPoint(activeControlPoint, pos);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        activeControlPoint = -1;
    }

    private Point2D pixel2Point(Point pos) {
        int zoom = editorModel.getZoom();
        double ratio = editorModel.getDefaultSize() / 100. * zoom;
        LineEditorContentView lineEditorContentView = dialog.getLineEditorContentView();
        Dimension size = lineEditorContentView.getSize();
        double x = (pos.getX() - size.getWidth() / 2) / ratio;
        double y = (size.getHeight() / 2 - pos.getY()) / ratio;

        return new Point2D.Double(x, y);
    }
}
