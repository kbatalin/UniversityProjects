package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.EditorController;
import ru.nsu.fit.g14205.batalin.models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

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

        for (LineProperties lineProperties : applicationProperties.getLineProperties()) {
            lineProperties.addObserver(LineProperties.Event.CONTROL_POINTS_CHANGED, LineEditorContentView.this::repaint);
            lineProperties.addObserver(LineProperties.Event.COLOR_CHANGED, LineEditorContentView.this::repaint);
        }

        applicationProperties.addObserver(ApplicationProperties.Event.LINE_PROPERTIES_ADDED, () -> {
            java.util.List<LineProperties> lineProperties = applicationProperties.getLineProperties();
            LineProperties properties = lineProperties.get(lineProperties.size() - 1);
            properties.addObserver(LineProperties.Event.CONTROL_POINTS_CHANGED, LineEditorContentView.this::repaint);
            properties.addObserver(LineProperties.Event.COLOR_CHANGED, LineEditorContentView.this::repaint);
        });

        editorModel.addObserver(EditorModel.Event.ACTIVE_LINE_CHANGED, this::repaint);
        editorModel.addObserver(EditorModel.Event.ZOOM_CHANGED, this::repaint);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                editorController.onMousePressed(mouseEvent);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                editorController.onMouseReleased(mouseEvent);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

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

        int xCenter = image.getWidth() / 2;
        int yCenter = image.getHeight() / 2;
        graphics.drawLine(0, yCenter, image.getWidth(), yCenter);
        graphics.drawLine(xCenter, 0, xCenter, image.getHeight());

        int size = editorModel.getDefaultSize();

        for(int x = size - (yCenter % size); x < image.getWidth(); x += size) {
            graphics.drawLine(x, yCenter - 2, x, yCenter + 2);
        }

        for(int y = size - (xCenter % size); y < image.getHeight(); y += size) {
            graphics.drawLine(xCenter - 2, y, xCenter + 2, y);
        }
    }

    private void paintLine(BufferedImage image) {
        int zoom = editorModel.getZoom();
        int currentLine = editorModel.getCurrentLine();
        LineProperties lineProperties = applicationProperties.getLineProperties().get(currentLine);

        Dimension size = getSize();
        double ratio = editorModel.getDefaultSize() / 100. * zoom;

        int lineColor = lineProperties.getColor().getRGB();

        double dt = 1 / (lineProperties.getLength() * ratio * 10);
        for(double t = 0.; Double.compare(t, 1.) <= 0; t += dt) {
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
        double ratio = editorModel.getDefaultSize() / 100. * zoom;

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
