package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.WireframeController;
import ru.nsu.fit.g14205.batalin.models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kir55rus on 11.04.17.
 */
public class WorkspaceView extends JComponent {
    private WireframeController wireframeController;
    private int margins = 10;

    public WorkspaceView(WireframeController wireframeController) {
        this.wireframeController = wireframeController;

        ApplicationProperties applicationProperties = wireframeController.getApplicationProperties();
        CameraProperties cameraProperties = applicationProperties.getCameraProperties();
        ViewPyramidProperties pyramidProperties = applicationProperties.getViewPyramidProperties();

        cameraProperties.addObserver(CameraProperties.Event.UP_VECTOR_CHANGED, this::repaint);
        cameraProperties.addObserver(CameraProperties.Event.VIEW_POINT_CHANGED, this::repaint);
        cameraProperties.addObserver(CameraProperties.Event.CAMERA_POSITION_CHANGED, this::repaint);

        pyramidProperties.addObserver(ViewPyramidProperties.Event.FRONT_PLANE_SIZE_CHANGED, this::repaint);
        pyramidProperties.addObserver(ViewPyramidProperties.Event.FRONT_PLANE_DISTANCE_CHANGED, this::repaint);
        pyramidProperties.addObserver(ViewPyramidProperties.Event.BACK_PLANE_DISTANCE_CHANGED, this::repaint);

        applicationProperties.getScene().addObserver(PaintedFigure.Event.FIGURE_CHANGED, this::repaint);

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                wireframeController.onMouseWheelMoved(mouseWheelEvent);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                wireframeController.onMousePressed(mouseEvent);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                wireframeController.onMouseReleased(mouseEvent);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                wireframeController.onMouseDragged(mouseEvent);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Dimension componentSize = getSize();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, componentSize.width, componentSize.height);

        ApplicationProperties applicationProperties = wireframeController.getApplicationProperties();
        ViewPyramidProperties viewPyramid = applicationProperties.getViewPyramidProperties();
        CameraProperties camera = applicationProperties.getCameraProperties();

        double viewPortSizeRatio = Math.min((componentSize.getWidth() - 2*margins) / viewPyramid.getFrontPlaneWidth(),
                (componentSize.getHeight() - 2* margins) / viewPyramid.getFrontPlaneHeight());
        int viewPortWidth = (int)(viewPyramid.getFrontPlaneWidth() * viewPortSizeRatio);
        int viewPortHeight = (int)(viewPyramid.getFrontPlaneHeight() * viewPortSizeRatio);
        Rectangle viewPort = new Rectangle((componentSize.width - viewPortWidth) / 2, (componentSize.height - viewPortHeight) / 2, viewPortWidth, viewPortHeight);

        graphics.setColor(Color.BLUE);
        graphics.drawRect(viewPort.x, viewPort.y, viewPort.width, viewPort.height);


        PaintedFigure scene = applicationProperties.getScene();

        Matrix worldToCamMatrix = camera.getWorldToCamMatrix();
        Matrix projectionMatrix = viewPyramid.getProjectionMatrix();
        Matrix sceneTransformMatrix = projectionMatrix.multiply(worldToCamMatrix);

        Matrix scale = new Matrix(3, 3, new double[]{
                viewPortSizeRatio, 0, 0,
                0, viewPortSizeRatio, 0,
                0, 0, 1
        });

        Matrix offset = new Matrix(3, 3, new double[]{
                1, 0, componentSize.getWidth() / 2,
                0, -1, componentSize.getHeight() / 2,
                0, 0, 1,
        });

        Matrix displayTransform = offset.multiply(scale);

        drawFigure(graphics, scene, sceneTransformMatrix, displayTransform);
    }

    private void drawAxes(Graphics graphics, Matrix csTransform, Matrix displayTransform) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setStroke(new BasicStroke(3));

        graphics.setColor(Color.RED);
        drawSegment(graphics, new Segment(new Point3D(0, 0, 0), new Point3D(1, 0, 0)), csTransform, displayTransform);

        graphics.setColor(Color.GREEN);
        drawSegment(graphics, new Segment(new Point3D(0, 0, 0), new Point3D(0, 1, 0)), csTransform, displayTransform);

        graphics.setColor(Color.BLUE);
        drawSegment(graphics, new Segment(new Point3D(0, 0, 0), new Point3D(0, 0, 1)), csTransform, displayTransform);

        graphics2D.setStroke(new BasicStroke(1));
    }

    private void drawFigure(Graphics graphics, PaintedFigure figure, Matrix csTransform, Matrix displayTransform) {

        Iterator<PaintedFigure> figureIterator = figure.figures();
        Matrix transformMatrix = figure.getCoordinateSystem().getTransformMatrix().multiply(csTransform);
        while (figureIterator.hasNext()) {
            PaintedFigure paintedFigure = figureIterator.next();

            drawFigure(graphics, paintedFigure, transformMatrix, displayTransform);
        }

        drawSegments(graphics, figure, transformMatrix, displayTransform);
        drawAxes(graphics, transformMatrix, displayTransform);
    }

    private void drawSegments(Graphics graphics, PaintedFigure figure, Matrix csTransform, Matrix displayTransform) {
        if (figure == null) {
            return;
        }

        graphics.setColor(Color.GRAY);
        Iterator<Segment> iterator = figure.segments();
        while (iterator.hasNext()) {
            Segment segment = iterator.next();
            drawSegment(graphics, segment, csTransform, displayTransform);
        }
    }

    private void drawSegment(Graphics graphics, Segment segment, Matrix csTransform, Matrix displayTransform) {
        Matrix pos1 = segment.getFirst().toMatrix4();
        pos1 = csTransform.multiply(pos1);
        pos1 = pos1.divide(pos1.get(0, 3));

        Matrix pos2 = segment.getSecond().toMatrix4();
        pos2 = csTransform.multiply(pos2);
        pos2 = pos2.divide(pos2.get(0, 3));

        pos1 = pos1.subMatrix(0, 0, 1, 3);
        pos2 = pos2.subMatrix(0, 0, 1, 3);
        pos1.set(0, 2, 1);
        pos2.set(0, 2, 1);
        pos1 = displayTransform.multiply(pos1);
        pos2 = displayTransform.multiply(pos2);

        int x0 = (int) Math.round(pos1.get(0, 0));
        int y0 = (int) Math.round(pos1.get(0, 1));
        int x1 = (int) Math.round(pos2.get(0, 0));
        int y1 = (int) Math.round(pos2.get(0, 1));
        graphics.drawLine(x0, y0, x1, y1);
    }
}
