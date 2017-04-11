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

        System.out.println(controlPointIndex);
    }

    public void onMouseReleased(MouseEvent mouseEvent) {

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
