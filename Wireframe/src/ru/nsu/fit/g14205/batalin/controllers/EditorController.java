package ru.nsu.fit.g14205.batalin.controllers;

import org.omg.CORBA.PRIVATE_MEMBER;
import ru.nsu.fit.g14205.batalin.models.ApplicationProperties;
import ru.nsu.fit.g14205.batalin.models.EditorModel;
import ru.nsu.fit.g14205.batalin.models.LineProperties;
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
        this.applicationProperties = wireframeController.getApplicationProperties();
        this.editorModel = new EditorModel(applicationProperties);
    }

    public void run() {
        dialog = new EditorDialog(this);
        dialog.pack();
        dialog.setLocationRelativeTo(wireframeController.getWireframeView());
        dialog.setVisible(true);
    }

    public void onRedSpinnerChanged(int value) {
        LineProperties lineProperties = applicationProperties.getLineProperties().get(editorModel.getCurrentLine());
        Color color = lineProperties.getColor();
        lineProperties.setColor(new Color(value, color.getGreen(), color.getBlue()));
    }

    public void onGreenSpinnerChanged(int value) {
        LineProperties lineProperties = applicationProperties.getLineProperties().get(editorModel.getCurrentLine());
        Color color = lineProperties.getColor();
        lineProperties.setColor(new Color(color.getRed(), value, color.getBlue()));
    }

    public void onBlueSpinnerChanged(int value) {
        LineProperties lineProperties = applicationProperties.getLineProperties().get(editorModel.getCurrentLine());
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
        LineProperties lineProperties = applicationProperties.getLineProperties().get(editorModel.getCurrentLine());
        Point2D pos = pixel2Point(mouseEvent.getPoint());
        int controlPointIndex = lineProperties.getControlPointId(pos);
        if (controlPointIndex == -1) {
            lineProperties.addControlPoint(pos);
            return;
        }

        activeControlPoint = controlPointIndex;
        System.out.println(controlPointIndex);
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        if (activeControlPoint == -1) {
            return;
        }

        LineProperties lineProperties = applicationProperties.getLineProperties().get(editorModel.getCurrentLine());
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
