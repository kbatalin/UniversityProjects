package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.IsolinesController;
import ru.nsu.fit.g14205.batalin.models.Area;
import ru.nsu.fit.g14205.batalin.models.PropertiesModel;
import ru.nsu.fit.g14205.batalin.models.painters.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.function.DoubleBinaryOperator;

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
        });

        PropertiesModel properties = isolinesController.getApplicationProperties();
        properties.addObserver(PropertiesModel.Event.CELLS_COUNT_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.AREA_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.PAINTER_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.ISOLINES_SHOWN_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.GRID_SHOWN_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.FUNCTION_CHANGED, this::repaint);
        properties.addObserver(PropertiesModel.Event.COLORS_CHANGED, this::repaint);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        PropertiesModel properties = isolinesController.getApplicationProperties();
        Dimension mapSize = getSize();
        Painter painter = properties.getPainter();
        BufferedImage map = painter.draw(properties.getMainFunction(), properties, mapSize);

        if (isolinesController.getApplicationProperties().isGridShown()) {
            paintGrid(map);
        }

        paintIsolines(map);

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
            System.out.println("y: " + crd);
        }

        double widthRatio = (double) map.getWidth() / applicationProperties.getVerticalCellsCount();
        for(int i = 1, count = applicationProperties.getVerticalCellsCount(); i < count; ++i) {
            int crd = (int) (i * widthRatio);
            graphics.drawLine(crd, 0, crd, map.getWidth());
            System.out.println("x: " + crd);
        }
    }

    private void paintIsolines(BufferedImage map) {
        PropertiesModel applicationProperties = isolinesController.getApplicationProperties();
        double[] isolinesValues = applicationProperties.getIsolinesValues();
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
                Point displayPos = new Point(
                        (int) (x * displayCellWidth),
                        (int) (y * displayCellHeight)
                );

                Point2D.Double realPos = new Point2D.Double(
                        displayPos.getX() / widthRatio + area.first.getX(),
                        displayPos.getY() / heightRatio + area.first.getY()
                );

//                System.out.println(x + " " +y +": " + displayPos);

                double f1 = function.applyAsDouble(realPos.getX(), realPos.getY());
                double f2 = function.applyAsDouble(realPos.getX() + realCellWidth, realPos.getY());
                double f3 = function.applyAsDouble(realPos.getX() + realCellWidth, realPos.getY() + realCellHeight);
                double f4 = function.applyAsDouble(realPos.getX(), realPos.getY() + realCellHeight);

                System.out.println(x + " " + y +"("+realPos+")" + ": " + f1 + "; " + f2 + "; " + f3 + "; " + f4);
//                System.out.println(x + " " + y);
                for(double isolineValue : isolinesValues) {
                    paintIsoline(map.getSubimage(displayPos.x, displayPos.y, (int) displayCellWidth, (int) displayCellHeight),
                            new double[]{f1, f2, f3, f4}, isolineValue);
                }
            }
        }
    }

    private void paintIsoline(BufferedImage image, double[] f, double value) {
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

//        System.out.println(res);

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
                a = new Point(0, (int) (image.getHeight() * (f[0] - value) / (f[0] - f[3])));
                b = new Point((int) (image.getWidth() * (value - f[3]) / (f[2] - f[3])), image.getHeight());
                break;

            case 2:
            case 13:
            a = new Point(image.getWidth(), (int) (image.getHeight() * (f[1] - value) / (f[1] - f[2])));
            b = new Point((int) (image.getWidth() * (f[3] - value) / (f[3] - f[2])), image.getHeight());
                break;

            case 3:
            case 12:
                a = new Point(0, (int) (image.getHeight() * (f[0] - value) / (f[0] - f[3])));
                b = new Point(image.getWidth(), (int) (image.getHeight() * (f[1] - value) / (f[1] - f[2])));
                break;

            case 4:
            case 11:
                a = new Point((int) (image.getWidth() * (f[0] - value) / (f[0] - f[1])), 0);
                b = new Point(image.getWidth(), (int) (image.getHeight() * (value - f[1]) / (f[2] - f[1])));
                break;

            case 5:
            case 10: {
                double center = (f[0] + f[1] + f[2] + f[3]) / 4;
                if(Double.compare(value, center) == Double.compare(value, f[0])) {
                    a = new Point(0, (int) (image.getHeight() * (value - f[3]) / (f[0] - f[3])));
                    b = new Point((int) (image.getWidth() * (value - f[3]) / (f[2] - f[3])), image.getHeight());
                    c = new Point((int) (image.getWidth() * (value - f[1]) / (f[0] - f[1])), 0);
                    d = new Point(image.getWidth(), (int) (image.getHeight() * (value - f[1]) / (f[2] - f[1])));
                } else {
                    a = new Point(image.getWidth(), (int) (image.getHeight() * (value - f[2]) / (f[1] - f[2])));
                    b = new Point((int) (image.getWidth() * (value - f[2]) / (f[3] - f[2])), image.getHeight());
                    c = new Point((int) (image.getWidth() * (value - f[0]) / (f[1] - f[0])), 0);
                    d = new Point(0, (int) (image.getHeight() * (value - f[0]) / (f[3] - f[0])));
                }
                break;
            }

            case 6:
            case 9:
            a = new Point((int) (image.getWidth() * (value - f[0]) / (f[1] - f[0])), 0);
            b = new Point((int) (image.getWidth() * (value - f[3]) / (f[2] - f[3])), image.getHeight());
            break;

            case 7:
            case 8:
            a = new Point((int) (image.getWidth() * (value - f[0]) / (f[1] - f[0])), 0);
            b = new Point(0, (int) (image.getHeight() * (value - f[0]) / (f[3] - f[0])));
                break;
        }

        if (a != null && b != null) {
//            System.out.println("Yes!");
            image.createGraphics().drawLine(a.x, a.y, b.x, b.y);
        }
        if (c != null && d != null) {
            image.createGraphics().drawLine(c.x, c.y, d.x, d.y);

        }
    }
}
