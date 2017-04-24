package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.*;
import ru.nsu.fit.g14205.batalin.views.EditorDialog;
import ru.nsu.fit.g14205.batalin.views.WireframeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

/**
 * Created by kir55rus on 11.04.17.
 */
public class WireframeController {
    private ApplicationProperties applicationProperties;
    private Point2D prevPos;

    private WireframeView wireframeView;

    public void run() {
        applicationProperties = new SimpleApplicationProperties();

        LineProperties lineProperties = new BSplineProperties(applicationProperties);
        lineProperties.setColor(Color.GREEN);
        lineProperties.addControlPoint(new Point2D.Double(-3, -3));
        lineProperties.addControlPoint(new Point2D.Double(-2, -3));
        lineProperties.addControlPoint(new Point2D.Double(-1, -3));
        lineProperties.addControlPoint(new Point2D.Double(0, 0));
        lineProperties.addControlPoint(new Point2D.Double(1, -3));
        lineProperties.addControlPoint(new Point2D.Double(2, -3));
        lineProperties.addControlPoint(new Point2D.Double(3, -3));
        lineProperties.addControlPoint(new Point2D.Double(4, -3));

        applicationProperties.addLineProperties(lineProperties);

        wireframeView = new WireframeView(this);
        wireframeView.setVisible(true);
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    public WireframeView getWireframeView() {
        return wireframeView;
    }

    public void onMouseWheelMoved(MouseWheelEvent event) {
        ViewPyramidProperties pyramidProperties = applicationProperties.getViewPyramidProperties();
        double zf = pyramidProperties.getFrontPlaneDistance();
        zf += 0.5 * event.getWheelRotation();
        pyramidProperties.setFrontPlaneDistance(zf);
    }

    public void onMousePressed(MouseEvent mouseEvent) {
        prevPos = mouseEvent.getPoint();
    }

    public void onMouseReleased(MouseEvent mouseEvent) {
        prevPos = null;
    }

    public void onMouseDragged(MouseEvent mouseEvent) {
        Point2D pos = mouseEvent.getPoint();
        if (prevPos == null) {
            prevPos = pos;
            return;
        }

        double x = (pos.getX() - prevPos.getX()) * 0.5;
        double y = (pos.getY() - prevPos.getY()) * 0.5;

    }

    public void onLineEditButtonClicked() {
        EditorController editorController = new EditorController(this);
        editorController.run();
    }

    public void onEnterToolbarButton(MouseEvent event) {
        Component component = event.getComponent();
        if (!(component instanceof JComponent)) {
            return;
        }

        JComponent button = ((JComponent) component);

        wireframeView.getStatusBarView().setMessage(button.getToolTipText());
    }

    public void onExitToolbarButton(MouseEvent event) {
        wireframeView.getStatusBarView().setMessage("");
    }
}
