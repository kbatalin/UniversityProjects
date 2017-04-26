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
import java.util.List;

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

        for (FigureProperties figureProperties : applicationProperties.getFigureProperties()) {
            LineProperties lineProperties = figureProperties.getLineProperties();
            lineProperties.addObserver(LineProperties.Event.CONTROL_POINTS_CHANGED, LineEditorContentView.this::repaint);
            lineProperties.addObserver(LineProperties.Event.COLOR_CHANGED, LineEditorContentView.this::repaint);
        }

        applicationProperties.addObserver(ApplicationProperties.Event.FIGURE_PROPERTIES_ADDED, () -> {
            List<FigureProperties> figureProperties = applicationProperties.getFigureProperties();
            LineProperties properties = figureProperties.get(figureProperties.size() - 1).getLineProperties();
            properties.addObserver(LineProperties.Event.CONTROL_POINTS_CHANGED, LineEditorContentView.this::repaint);
            properties.addObserver(LineProperties.Event.COLOR_CHANGED, LineEditorContentView.this::repaint);
        });

        editorModel.addObserver(EditorModel.Event.ACTIVE_LINE_CHANGED, this::repaint);
        editorModel.addObserver(EditorModel.Event.ZOOM_CHANGED, this::repaint);
        applicationProperties.addObserver(ApplicationProperties.Event.AREA_CHANGED, this::repaint);
        editorModel.addObserver(EditorModel.Event.OFFSET_CHANGED, this::repaint);

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

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                editorController.onMouseDragged(mouseEvent);
            }
        });

        editorModel.addObserver(EditorModel.Event.RISKS_SHOWN_CHANGES, this::repaint);
        editorModel.addObserver(EditorModel.Event.CONTROL_POINTS_SHOWN_CHENGED, this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Dimension size = getSize();
        BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

        paintCoordinateAxes(image);

        paintLine(image);

        if (editorModel.isRisksShown()) {
            paintLineRisks(image);
        }

        if (editorModel.isControlPointsShown()) {
            paintControlPoints(image);
        }

        graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
    }

    private void paintLineRisks(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();

        int zoom = editorModel.getZoom();
        double ratio = editorModel.getDefaultSize() / 100. * zoom;
        Point2D offset = editorModel.getOffset();
        Dimension size = getSize();
        Area area = applicationProperties.getArea();

        int currentLine = editorModel.getCurrentFigure();
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(currentLine);
        LineProperties lineProperties = figureProperties.getLineProperties();

        int riskSize = 6;
        double dt = 1. / (lineProperties.getControlPointsCount() - 1);
        for(double t = 0.; Double.compare(t, 1.) <= 0; t += dt) {
            Point2D pos = lineProperties.getPoint(t);
            if (pos == null) {
                continue;
            }
            int x = (int)Math.round(pos.getX() * ratio + size.getWidth() / 2 + offset.getX() * ratio);
            int y = (int)Math.round(size.getHeight() - pos.getY() * ratio - size.getHeight() / 2 + offset.getY() * ratio);
            if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
                continue;
            }

            if(t < area.first.getX() || t > area.second.getX()) {
                graphics.setPaint(Color.GRAY);
            } else {
                graphics.setPaint(Color.RED);
            }

            graphics.fillOval(x - riskSize / 2, y - riskSize / 2, riskSize, riskSize);
        }
    }

    private void paintCoordinateAxes(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(Color.GRAY);

        double ratio = editorModel.getDefaultSize() / 100. * editorModel.getZoom();

        Point2D offset = editorModel.getOffset();
        int widthCenter = (int) Math.round(image.getWidth() / 2. + offset.getX() * ratio);
        int heightCenter = (int) Math.round(image.getHeight() / 2. + offset.getY() * ratio);
        graphics.drawLine(0, heightCenter, image.getWidth(), heightCenter);
        graphics.drawLine(widthCenter, 0, widthCenter, image.getHeight());

        int zoom = editorModel.getZoom();
        int size = (int)Math.round(editorModel.getDefaultSize() / 100. * zoom);

        for(int x = (widthCenter % size); x < image.getWidth(); x += size) {
            graphics.drawLine(x, heightCenter - 2, x, heightCenter + 2);
        }

        for(int y = (heightCenter % size); y < image.getHeight(); y += size) {
            graphics.drawLine(widthCenter - 2, y, widthCenter + 2, y);
        }
    }

    private void paintLine(BufferedImage image) {
        int zoom = editorModel.getZoom();
        int currentLine = editorModel.getCurrentFigure();
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(currentLine);
        LineProperties lineProperties = figureProperties.getLineProperties();
        Area area = applicationProperties.getArea();

        Dimension size = getSize();
        double ratio = editorModel.getDefaultSize() / 100. * zoom;

        int lineColor = lineProperties.getColor().getRGB();

        Point2D offset = editorModel.getOffset();
        double dt = 1 / (lineProperties.getLength() * ratio * 10);
        for(double t = 0.; Double.compare(t, 1.) <= 0; t += dt) {
            Point2D pos = lineProperties.getPoint(t);
            if (pos == null) {
                continue;
            }
            int x = (int)Math.round(pos.getX() * ratio + size.getWidth() / 2 + offset.getX() * ratio);
            int y = (int)Math.round(size.getHeight() - pos.getY() * ratio - size.getHeight() / 2 + offset.getY() * ratio);
            if (x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) {
                continue;
            }

            if(t < area.first.getX() || t > area.second.getX()) {
                image.setRGB(x, y, Color.GRAY.getRGB());
            } else {
                image.setRGB(x, y, lineColor);
            }
        }
    }

    private void paintControlPoints(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.CYAN);
        int zoom = editorModel.getZoom();
        int currentLine = editorModel.getCurrentFigure();
        FigureProperties figureProperties = applicationProperties.getFigureProperties().get(currentLine);
        LineProperties lineProperties = figureProperties.getLineProperties();

        Dimension size = getSize();
        double ratio = editorModel.getDefaultSize() / 100. * zoom;
        Point2D offset = editorModel.getOffset();

        Point prevPos = null;
        int ovalSize = (int)Math.round(applicationProperties.getControlPointRadius() * ratio) * 2;
        Iterator<Point2D> controlPointsIterator = lineProperties.getControlPointsIterator();
        while (controlPointsIterator.hasNext()) {
            Point2D pos = controlPointsIterator.next();
            int x = (int)Math.round(pos.getX() * ratio + size.getWidth() / 2 - ovalSize / 2 + offset.getX() * ratio);
            int y = (int)Math.round(size.getHeight() - pos.getY() * ratio - size.getHeight() / 2 - ovalSize / 2 + offset.getY() * ratio);

            if (prevPos != null) {
                graphics.drawLine(x + ovalSize / 2, y + ovalSize / 2, prevPos.x + ovalSize / 2, prevPos.y + ovalSize / 2);
            }
            prevPos = new Point(x, y);

            graphics.drawOval(x, y, ovalSize, ovalSize);
        }
    }
}
