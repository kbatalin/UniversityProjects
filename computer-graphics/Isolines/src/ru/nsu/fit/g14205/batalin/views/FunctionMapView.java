package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.AffineTransformOp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Collectors;

/**
 * Created by kir55rus on 01.04.17.
 */
public class FunctionMapView extends JComponent {
    private IsolinesController isolinesController;

    public FunctionMapView(IsolinesController isolinesController) {
        this.isolinesController = isolinesController;

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent mouseEvent) {
                isolinesController.onMouseMoved(mouseEvent);
            }

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                isolinesController.onMouseDragged(mouseEvent);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                isolinesController.onMousePressed(mouseEvent);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                isolinesController.onMouseReleased(mouseEvent);
            }
        });

        PropertiesModel properties = isolinesController.getApplicationProperties();
        properties.addObserver(PropertiesModel.Event.CELLS_COUNT_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.AREA_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.PAINTER_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.ISOLINES_SHOWN_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.GRID_SHOWN_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.FUNCTION_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.COLORS_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.ISOLINES_VALUES_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.ENTRY_POINTS_SHOWN_CHANGED, this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        PropertiesModel properties = isolinesController.getApplicationProperties();
        Dimension mapSize = getSize();
        Painter painter = properties.getPainter();
        BufferedImage map = painter.draw(properties.getMainFunction(), properties, mapSize);

        if (properties.isGridShown()) {
            paintGrid(map);
        }

        if(properties.isIsolinesShown()) {
            paintIsolines(map);
        }

        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -map.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        map = op.filter(map, null);
        graphics.drawImage(map, 0, 0, null);
    }

    private void paintGrid(BufferedImage map) {
        PropertiesModel applicationProperties = isolinesController.getApplicationProperties();

        Graphics2D graphics = map.createGraphics();
        graphics.setPaint(Color.BLACK);
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
        graphics.setStroke(dashed);

        double heightRatio = (double) map.getHeight() / applicationProperties.getHorizontalCellsCount();
        for(int i = 1, count = applicationProperties.getHorizontalCellsCount(); i < count; ++i) {
            int crd = (int) (i * heightRatio);
            graphics.drawLine(0, crd, map.getWidth(), crd);
        }

        double widthRatio = (double) map.getWidth() / applicationProperties.getVerticalCellsCount();
        for(int i = 1, count = applicationProperties.getVerticalCellsCount(); i < count; ++i) {
            int crd = (int) (i * widthRatio);
            graphics.drawLine(crd, 0, crd, map.getWidth());
        }
    }

    private void paintIsolines(BufferedImage map) {
        PropertiesModel applicationProperties = isolinesController.getApplicationProperties();
        List<Double> isolinesValues = new ArrayList<>(applicationProperties.getIsolinesValues());
        isolinesValues.addAll(Arrays.stream(applicationProperties.getValues()).boxed().collect(Collectors.toList()));
        Area area = applicationProperties.getArea();
        Dimension areaSize = area.toDimension();
        double widthRatio = map.getWidth() / areaSize.getWidth();
        double heightRatio = map.getHeight() / areaSize.getHeight();
        DoubleBinaryOperator function = applicationProperties.getMainFunction();
        double displayCellWidth = (double) map.getWidth() / applicationProperties.getVerticalCellsCount();
        double displayCellHeight = (double) map.getHeight() / applicationProperties.getHorizontalCellsCount();
        double realCellWidth = displayCellWidth / widthRatio;
        double realCellHeight = displayCellHeight / heightRatio;

        Graphics2D graphics = map.createGraphics();
        graphics.setPaint(applicationProperties.getIsolinesColor());
        Stroke solid = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9, 0}, 0);
        graphics.setStroke(solid);

        for(int y = 0; y < applicationProperties.getHorizontalCellsCount(); ++y) {
            for(int x = 0; x < applicationProperties.getVerticalCellsCount(); ++x) {
                Point2D displayPos = new Point2D.Double(
                        x * displayCellWidth,
                        y * displayCellHeight
                );

                Point2D.Double realPos = new Point2D.Double(
                        displayPos.getX() / widthRatio + area.first.getX(),
                        displayPos.getY() / heightRatio + area.first.getY()
                );

                double f1 = function.applyAsDouble(realPos.getX(), realPos.getY());
                double f2 = function.applyAsDouble(realPos.getX() + realCellWidth, realPos.getY());
                double f3 = function.applyAsDouble(realPos.getX() + realCellWidth, realPos.getY() + realCellHeight);
                double f4 = function.applyAsDouble(realPos.getX(), realPos.getY() + realCellHeight);

                for(double isolineValue : isolinesValues) {
                    double subImgWidth = Math.min(map.getWidth() - displayPos.getX(), (((x + 1) * displayCellWidth)) - displayPos.getX());
                    double subImgHeight = Math.min(map.getHeight() - displayPos.getY(), (((y + 1) * displayCellHeight)) - displayPos.getY());
                    paintIsoline(map, displayPos, subImgWidth, subImgHeight,
                            new double[]{f1, f2, f3, f4}, isolineValue);
                }
            }
        }
    }

    private void paintIsoline(BufferedImage image, Point2D startPos, double width, double height, double[] f, double value) {
        double eps = 1e-6;
        for(int i = 0; i < f.length; ++i) {
            if (Double.compare(f[i], value) == 0) {
                value += Double.compare(f[i], f[(i + 1) % f.length]) < 0 ? eps : -eps;
            }

        }

        int res = 0;
        for(int i = 0; i < f.length; ++i) {
            if (Double.compare(f[i], value) > 0) {
                res += (1 << (3 - i));
            }
        }

        Point a = null;
        Point b = null;
        Point c = null;
        Point d = null;
        switch (res) {
            case 0:
            case 15:
                break;

            case 1:
            case 14:
                a = new Point((int)Math.round(startPos.getX()), (int)Math.round(startPos.getY() + height * (value - f[0]) / (f[3] - f[0])));
                b = new Point((int)Math.round(startPos.getX() + width * (value - f[3]) / (f[2] - f[3])), (int)Math.round(startPos.getY() + height));
                break;

            case 2:
            case 13:
                a = new Point((int)Math.round(startPos.getX() + width), (int)Math.round(startPos.getY() + height * (value - f[1]) / (f[2] - f[1])));
                b = new Point((int)Math.round(startPos.getX() + width * (value - f[3]) / (f[2] - f[3])), (int)Math.round(startPos.getY() + height));
                break;

            case 3:
            case 12:
                a = new Point((int)Math.round(startPos.getX()), (int)Math.round(startPos.getY() + height * (value - f[0]) / (f[3] - f[0])));
                b = new Point((int)Math.round(startPos.getX() + width), (int)Math.round(startPos.getY() + height * (value - f[1]) / (f[2] - f[1])));
                break;

            case 4:
            case 11:
                a = new Point((int)Math.round(startPos.getX() + width * (value - f[0]) / (f[1] - f[0])), (int)Math.round(startPos.getY()));
                b = new Point((int)Math.round(startPos.getX() + width), (int)Math.round(startPos.getY() + height * (value - f[1]) / (f[2] - f[1])));
                break;

            case 5:
            case 10: {
                double center = (f[0] + f[1] + f[2] + f[3]) / 4;
                if(Double.compare(value, center) == Double.compare(value, f[0])) {
                    a = new Point((int)Math.round(startPos.getX()), (int)Math.round(startPos.getY() + height * (value - f[0]) / (f[3] - f[0])));
                    b = new Point((int)Math.round(startPos.getX() + width * (value - f[3]) / (f[2] - f[3])), (int)Math.round(startPos.getY() + height));
                    c = new Point((int)Math.round(startPos.getX() + width * (value - f[0]) / (f[1] - f[0])), (int)Math.round(startPos.getY()));
                    d = new Point((int)Math.round(startPos.getX() + width), (int)Math.round(startPos.getY() + height * (value - f[1]) / (f[2] - f[1])));
                } else {
                    a = new Point((int)Math.round(startPos.getX() + width), (int)Math.round(startPos.getY() + height * (value - f[1]) / (f[2] - f[1])));
                    b = new Point((int)Math.round(startPos.getX() + width * (value - f[3]) / (f[2] - f[3])), (int)Math.round(startPos.getY() + height));
                    c = new Point((int)Math.round(startPos.getX() + width * (value - f[0]) / (f[1] - f[0])), (int)Math.round(startPos.getY()));
                    d = new Point((int)Math.round(startPos.getX()), (int)Math.round(startPos.getY() + height * (value - f[0]) / (f[3] - f[0])));
                }
                break;
            }

            case 6:
            case 9:
                a = new Point((int)Math.round(startPos.getX() + width * (value - f[0]) / (f[1] - f[0])), (int)Math.round(startPos.getY()));
                b = new Point((int)Math.round(startPos.getX() + width * (value - f[3]) / (f[2] - f[3])), (int)Math.round(startPos.getY() + height));
                break;

            case 7:
            case 8:
                a = new Point((int)Math.round(startPos.getX() + width * (value - f[0]) / (f[1] - f[0])), (int)Math.round(startPos.getY()));
                b = new Point((int)Math.round(startPos.getX()), (int)Math.round(startPos.getY() + height * (value - f[0]) / (f[3] - f[0])));
                break;
        }

        Graphics2D graphics = image.createGraphics();
        graphics.setPaint(isolinesController.getApplicationProperties().getIsolinesColor());
        if (a != null && b != null) {
            graphics.drawLine(a.x, a.y, b.x, b.y);
        }
        if (c != null && d != null) {
            graphics.drawLine(c.x, c.y, d.x, d.y);
        }

        if (isolinesController.getApplicationProperties().isEntryPointsShown()) {
            paintEntryPoint(graphics, a);
            paintEntryPoint(graphics, b);
            paintEntryPoint(graphics, c);
            paintEntryPoint(graphics, d);
        }
    }

    private void paintEntryPoint(Graphics2D graphics, Point pos) {
        if (pos == null) {
            return;
        }
        graphics.fillOval(pos.x - 5, pos.y - 5, 10, 10);
    }
}
