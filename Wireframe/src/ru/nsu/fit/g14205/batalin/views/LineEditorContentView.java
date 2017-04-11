package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.EditorController;
import ru.nsu.fit.g14205.batalin.models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;

/**
 * Created by kir55rus on 12.04.17.
 */
public class LineEditorContentView extends JPanel {
    private EditorController editorController;
    private ApplicationProperties applicationProperties;
    private EditorModel editorModel;

    public LineEditorContentView(EditorController editorController) {
        this.editorController = editorController;

        applicationProperties = editorController.getApplicationProperties();
        editorModel = editorController.getEditorModel();

        editorModel.addObserver(EditorModel.Event.ACTIVE_LINE_CHANGED, this::repaint);
        editorModel.addObserver(EditorModel.Event.ZOOM_CHANGED, this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        SimpleApplicationProperties applicationProperties = new SimpleApplicationProperties();
        LineProperties lineProperties = new BSplineProperties(applicationProperties);

        Dimension size = getSize();
        BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

        paintCoordinateAxes(image);

        paintLine(image);

        paintControlPoints(image);

        graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);

    }

    private void paintCoordinateAxes(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(Color.GRAY);

        graphics.drawLine(0, image.getHeight() / 2, image.getWidth(), image.getHeight() / 2);
        graphics.drawLine(image.getWidth() / 2, 0, image.getWidth() / 2, image.getHeight());
    }

    private void paintLine(BufferedImage image) {
        int zoom = editorModel.getZoom();
        int currentLine = editorModel.getCurrentLine();
        LineProperties lineProperties = applicationProperties.getLineProperties().get(currentLine);

        Dimension size = getSize();
        Area area = lineProperties.getArea();
        double ratio = Math.min(size.getWidth(), size.getHeight()) / Math.min(area.getWidth(), area.getHeight()) / 100. * zoom;

        int lineColor = lineProperties.getColor().getRGB();

        for(double t = 0.; Double.compare(t, 1.) <= 0; t += 0.001) {
            Point2D pos = lineProperties.getPoint(t);
            int x = (int)Math.round(pos.getX() * ratio + size.getWidth() / 2);
            int y = (int)Math.round(size.getHeight() - pos.getY() * ratio - size.getHeight() / 2);
            if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
                System.out.println(x + " " + y);
                continue;
            }
            image.setRGB(x, y, lineColor);
        }
    }

    private void paintControlPoints(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        int zoom = editorModel.getZoom();
        int currentLine = editorModel.getCurrentLine();
        LineProperties lineProperties = applicationProperties.getLineProperties().get(currentLine);

        Dimension size = getSize();
        Area area = lineProperties.getArea();
        double ratio = Math.min(size.getWidth(), size.getHeight()) / Math.min(area.getWidth(), area.getHeight()) / 100. * zoom;

        int ovalSize = (int)Math.round(applicationProperties.getControlPointRadius() * ratio);
        Iterator<Point2D> controlPointsIterator = lineProperties.getControlPointsIterator();
        while (controlPointsIterator.hasNext()) {
            Point2D pos = controlPointsIterator.next();
            int x = (int)Math.round(pos.getX() * ratio + size.getWidth() / 2 - ovalSize / 2);
            int y = (int)Math.round(size.getHeight() - pos.getY() * ratio - size.getHeight() / 2 - ovalSize / 2);
            graphics.drawOval(x, y, ovalSize, ovalSize);
        }
    }
}
