package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.EditorDialog;
import ru.nsu.fit.g14205.batalin.views.LineEditorContentView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * Created by kir55rus on 12.04.17.
 */
public class EditorController {
    private WireframeController wireframeController;
    private ApplicationProperties applicationProperties;
    private EditorModel editorModel;
    private Point prevPos;

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

    public void onCXSpinnerChanged(double value) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();
        Point3D center = coordinateSystem.getCenter();
        Point3D newCenter = new Point3D(value, center.getY(), center.getZ());
        coordinateSystem.setCenter(newCenter);
    }

    public void onCYSpinnerChanged(double value) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();
        Point3D center = coordinateSystem.getCenter();
        Point3D newCenter = new Point3D(center.getX(), value, center.getZ());
        coordinateSystem.setCenter(newCenter);
    }

    public void onCZSpinnerChanged(double value) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();
        Point3D center = coordinateSystem.getCenter();
        Point3D newCenter = new Point3D(center.getX(), center.getY(), value);
        coordinateSystem.setCenter(newCenter);
    }

    public void onZfSpinnerChanged(double value) {
        ViewPyramidProperties viewPyramid = applicationProperties.getViewPyramidProperties();
        viewPyramid.setFrontPlaneDistance(value);
    }

    public void onZbSpinnerChanged(double value) {
        ViewPyramidProperties viewPyramid = applicationProperties.getViewPyramidProperties();
        viewPyramid.setBackPlaneDistance(value);
    }

    public void onSwSpinnerChanged(double value) {
        ViewPyramidProperties viewPyramid = applicationProperties.getViewPyramidProperties();
        viewPyramid.setFrontPlaneWidth(value);
    }

    public void onShSpinnerChanged(double value) {
        ViewPyramidProperties viewPyramid = applicationProperties.getViewPyramidProperties();
        viewPyramid.setFrontPlaneHeight(value);
    }

    public void onShowRisksCheckBoxChanged(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof JToggleButton)) {
            return;
        }

        JToggleButton button = (JToggleButton) sourceObj;
        editorModel.setRisksShown(button.isSelected());
    }

    public void onShowControlPointsCheckBoxChanged(ActionEvent actionEvent) {
        Object sourceObj = actionEvent.getSource();
        if (!(sourceObj instanceof JToggleButton)) {
            return;
        }

        JToggleButton button = (JToggleButton) sourceObj;
        editorModel.setControlPointsShown(button.isSelected());
    }

    public void onAlphaSpinnerChanged(double value) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();
        coordinateSystem.setAlphaAngle(value * Math.PI / 180);
    }

    public void onBetaSpinnerChanged(double value) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();
        coordinateSystem.setBetaAngle(value * Math.PI / 180);
    }

    public void onThetaSpinnerChanged(double value) {
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();
        coordinateSystem.setThetaAngle(value * Math.PI / 180);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isMiddleMouseButton(mouseEvent)) {
            prevPos = mouseEvent.getPoint();
            return;
        }

        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        LineProperties lineProperties = figureProperties.getLineProperties();
        Point2D pos = pixel2Point(mouseEvent.getPoint());
        int controlPointIndex = lineProperties.getControlPointId(pos);

        if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            if (controlPointIndex != -1) {
                lineProperties.delControlPoint(controlPointIndex);
            }
            return;
        }

        if (controlPointIndex != -1) {
            activeControlPoint = controlPointIndex;
            return;
        }

        Point2D prevPos = null;
        Iterator<Point2D> controlPointIterator = lineProperties.getControlPointsIterator();
        while (controlPointIterator.hasNext()) {
            Point2D currentPos = controlPointIterator.next();

            if (prevPos == null) {
                prevPos = currentPos;
                continue;
            }

            double distanceToSegment = distance(pos.getX(), pos.getY(), prevPos.getX(), prevPos.getY(), currentPos.getX(), currentPos.getY());
            if (Double.compare(distanceToSegment, applicationProperties.getControlPointRadius()) <= 0) {
                int index = lineProperties.getControlPointId(prevPos);
                if (index == -1) {
                    index = lineProperties.getControlPointId(currentPos);
                } else {
                    ++index;
                }
                if (index != -1) {
                    lineProperties.addControlPoint(index, pos);
                    activeControlPoint = index;
                    return;
                }
            }
            prevPos = currentPos;
        }

        lineProperties.addControlPoint(pos);
        activeControlPoint = lineProperties.getControlPointsCount() - 1;
    }

    private double distance(double x, double y, double x1, double y1, double x2, double y2) {
        double A = x - x1;
        double B = y - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        double xx;
        double yy;

        if (param < 0) {
            xx = x1;
            yy = y1;
        }
        else if (param > 1) {
            xx = x2;
            yy = y2;
        }
        else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = x - xx;
        double dy = y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        if (SwingUtilities.isMiddleMouseButton(mouseEvent)) {
            if (prevPos == null) {
                prevPos = mouseEvent.getPoint();
                return;
            }
            double ratio = editorModel.getDefaultSize() / 100. * editorModel.getZoom();
            Point pos = mouseEvent.getPoint();
            Point2D offset = editorModel.getOffset();
            Point2D newOffset = new Point2D.Double(
                    offset.getX() + (pos.getX() - prevPos.getX()) / ratio,
                    offset.getY() + (pos.getY() - prevPos.getY()) / ratio
            );
            editorModel.setOffset(newOffset);
            prevPos = pos;
            return;
        }

        if (activeControlPoint == -1) {
            return;
        }

        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(editorModel.getCurrentFigure());
        LineProperties lineProperties = figureProperties.getLineProperties();
        Point2D pos = pixel2Point(mouseEvent.getPoint());
        lineProperties.setControlPoint(activeControlPoint, pos);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        prevPos = null;
        activeControlPoint = -1;
    }

    private Point2D pixel2Point(Point pos) {
        int zoom = editorModel.getZoom();
        double ratio = editorModel.getDefaultSize() / 100. * zoom;
        Point2D offset = editorModel.getOffset();
        LineEditorContentView lineEditorContentView = dialog.getLineEditorContentView();
        Dimension size = lineEditorContentView.getSize();
        double x = (pos.getX() - size.getWidth() / 2) / ratio - offset.getX();
        double y = (size.getHeight() / 2 - pos.getY()) / ratio + offset.getY();

        return new Point2D.Double(x, y);
    }
}
