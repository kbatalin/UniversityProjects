package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.WireframeController;
import ru.nsu.fit.g14205.batalin.models.*;

import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
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

        int x0 = viewPort.x;
        int y0 = viewPort.y;
        int x1 = x0 + viewPort.width;
        int y1 = y0 + viewPort.height;
        Matrix displayTransform = new Matrix(4, 4, new double[]{
                (x1 - x0) / 2., 0, 0, (x0 + x1) / 2.,
                0, -(y1 - y0) / 2., 0, (y0 + y1) / 2.,
                0, 0, 1 / 2., 1 / 2.,
                0, 0, 0, 1
        });

        drawFigure(graphics, scene, sceneTransformMatrix, displayTransform);
    }

    private void drawFigure(Graphics graphics, PaintedFigure figure, Matrix csTransform, Matrix displayTransform) {
        Iterator<PaintedFigure> figureIterator = figure.figures();
        Matrix transformMatrix = csTransform.multiply(figure.getFigureProperties().getCoordinateSystem().getTransformMatrix());
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
        LineProperties lineProperties = figure.getFigureProperties().getLineProperties();
        if (lineProperties == null) {
            return;
        }

        graphics.setColor(lineProperties.getColor());
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

        Segment visibleSegment = clipping(new Point3D(pos1.subMatrix(0, 0, 1, 3)),
                new Point3D(pos2.subMatrix(0, 0, 1, 3)));
        if (visibleSegment == null) {
            return;
        }

        pos1 = displayTransform.multiply(visibleSegment.getFirst().toMatrix4());
        pos2 = displayTransform.multiply(visibleSegment.getSecond().toMatrix4());

        int x0 = (int) Math.round(pos1.get(0, 0));
        int y0 = (int) Math.round(pos1.get(0, 1));
        int x1 = (int) Math.round(pos2.get(0, 0));
        int y1 = (int) Math.round(pos2.get(0, 1));
        graphics.drawLine(x0, y0, x1, y1);
    }

    private Segment clipping(Point3D pos1, Point3D pos2) {
        boolean inViewPort1 = isInFrame(pos1);
        boolean inViewPort2 = isInFrame(pos2);

        if (inViewPort1 && inViewPort2) {
            return new Segment(pos1, pos2);
        }
        if (!inViewPort1 && !inViewPort2) {
            return null;
        }

        Line2D line = new Line2D.Double(pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY());
        Point2D[] corners = new Point2D[]{
                new Point2D.Double(1, 1),
                new Point2D.Double(1, -1),
                new Point2D.Double(-1, -1),
                new Point2D.Double(-1, 1),
        };

        //X, Y
        for(int i = 0; i < corners.length; ++i) {
            int next = (i + 1) % corners.length;
            Line2D border = new Line2D.Double(corners[i], corners[next]);
            if(!border.intersectsLine(line)) {
                continue;
            }

            Point2D intersectPoint = getIntersectPoint(line, border);
            if (inViewPort1) {
                return new Segment(pos1, new Point3D(intersectPoint));
            } else {
                return new Segment(pos2, new Point3D(intersectPoint));
            }
        }

        //Z
        double k = (pos2.getY() - pos1.getY()) / (pos2.getX() - pos1.getX());
        double b = pos1.getY() - k * pos1.getX();

        if (inViewPort1) {
            return getPartSegmentZ(pos1, pos2);
        }

        return getPartSegmentZ(pos2, pos1);
    }

    private Segment getPartSegmentZ(Point3D in, Point3D out) {
        double border = Double.compare(out.getZ(), -1.) < 0 ? -1. : 0;

        double t = (border - in.getZ()) / (out.getZ() - in.getZ());
        double x = in.getX() + (out.getX() - in.getX()) * t;
        double y = in.getY() + (out.getY() - in.getY()) * t;
        double z = border;

        return new Segment(in, new Point3D(x, y, z));
    }

    private Point2D getIntersectPoint(Line2D line1, Line2D line2) {
        double k = (line1.getP2().getY() - line1.getP1().getY()) / (line1.getP2().getX() - line1.getP1().getX());
        double b = line1.getP2().getY() - k * line1.getP2().getX();

        double x1 = line1.getX1();
        double y1 = line1.getY1();
        double x2 = line1.getX2();
        double y2 = line1.getY2();
        double x3 = line2.getX1();
        double y3 = line2.getY1();
        double x4 = line2.getX2();
        double y4 = line2.getY2();

        double x = (
                (x2 - x1) * (x3 * y4 - x4 * y3) - (x4 - x3) * (x1 * y2 - x2 * y1)
        ) /
                (
                        (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4)
                );
        double y = (
                (y3 - y4)*(x1*y2 - x2*y1) - (y1 - y2)*(x3*y4 - x4*y3)
        ) /
                (
                        (x1 - x2)*(y3 - y4) - (y1 - y2)*(x3 - x4)
                );

        return new Point2D.Double(x, y);
    }

    private boolean isInFrame(Point3D pos) {
        return Double.compare(pos.getX(), 1.) <= 0 &&
                Double.compare(pos.getX(), -1.) >= 0 &&
                Double.compare(pos.getY(), 1.) <= 0 &&
                Double.compare(pos.getY(), -1.) >= 0 &&
                Double.compare(pos.getZ(), -1.) >= 0 &&
                Double.compare(pos.getZ(), 0) <= 0;
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
}
