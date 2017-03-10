package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;
import ru.nsu.fit.g14205.batalin.models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Stack;

/**
 * Created by kir55rus on 14.02.17.
 */
public class FieldView extends JLabel {
    private LifeController lifeController;
    private IFieldModel fieldModel;
    private IPropertiesModel propertiesModel;

    private Dimension preferredSize;
    private int hexSize;
    private int hexIncircle;
    private int halfHexSize;
    private int backgroundOffset;
    private Color aliveColor = Color.GREEN;
    private Point[] firstHex;


    public FieldView(LifeController lifeController, IFieldModel fieldModel, IPropertiesModel propertiesModel) {
        this.lifeController = lifeController;
        this.fieldModel = fieldModel;
        this.propertiesModel = propertiesModel;

        updSize(propertiesModel);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                lifeController.onMousePressed(mouseEvent);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                lifeController.onMouseReleased(mouseEvent);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                lifeController.onMouseDragged(mouseEvent);
            }
        });

        addMouseWheelListener(lifeController::onMouseWheelMoved);

        propertiesModel.addObserver(PropertiesModelEvent.HEX_SIZE_CHANGED, () -> {
            updSize(propertiesModel);
            repaint();
        });

        propertiesModel.addObserver(PropertiesModelEvent.FIELD_SIZE_CHANGED, () -> {
            updSize(propertiesModel);
            repaint();
        });

        fieldModel.addObserver(FieldModelEvent.NEXT_STEP, this::repaint);
        fieldModel.addObserver(FieldModelEvent.FILED_CLEARED, this::repaint);
        fieldModel.addObserver(FieldModelEvent.CELL_STATE_CHANGED, this::repaint);
        propertiesModel.addObserver(PropertiesModelEvent.IMPACT_VISIBLE_CHANGED, this::repaint);
        propertiesModel.addObserver(PropertiesModelEvent.LINE_THICKNESS_CHANGED, this::repaint);
        propertiesModel.addObserver(PropertiesModelEvent.IMPACT_VALUE_CHANGED, this::repaint);
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Dimension fieldSize = fieldModel.getActiveField().getSize();
        Rectangle clipBounds = graphics.getClipBounds();

        BufferedImage background = new BufferedImage(clipBounds.width + backgroundOffset, clipBounds.height + backgroundOffset, BufferedImage.TYPE_INT_RGB);
        Graphics2D backgroundGraphics = background.createGraphics();
        backgroundGraphics.setPaint(Color.WHITE);
        backgroundGraphics.fillRect(0, 0, background.getWidth(), background.getHeight());

//        System.out.println(clipBounds);

        int x0 = Math.max(0, clipBounds.x / (2 * hexIncircle) - 1);
        int x1 = Math.min(fieldSize.width, (clipBounds.x + clipBounds.width) / (2 * hexIncircle) + 1);
        int y0 = Math.max(0, clipBounds.y / (3 * halfHexSize) - 1);
        int y1 = Math.min(fieldSize.height, (clipBounds.y + clipBounds.height) / (3 * halfHexSize) + 1);

//        System.out.println("x0: " + x0 + ", x1: " + x1 + ", y0: " + y0 + ", y1: " + y1);

        drawField(background, x0, y0, x1, y1);

        int offsetX = Math.max(0, clipBounds.x - (clipBounds.x % (2 * hexIncircle)) - (2 * hexIncircle));
        int offsetY = Math.max(0, clipBounds.y - (clipBounds.y % (3 * halfHexSize)) - (3 * halfHexSize));

        graphics.drawImage(background, offsetX, offsetY, null);
    }

    private void updSize(IPropertiesModel propertiesModel) {
        Dimension fieldSize = fieldModel.getActiveField().getSize();

        this.hexSize = propertiesModel.getHexSize();
        this.hexIncircle = propertiesModel.getHexIncircle();
        this.halfHexSize = this.hexSize / 2;
        this.backgroundOffset = 12 * this.halfHexSize;

        firstHex = new Point[6];
        firstHex[0] = new Point(hexIncircle, 0);
        firstHex[1] = new Point(2 * hexIncircle, halfHexSize);
        firstHex[2] = new Point(2 * hexIncircle, halfHexSize * 3);
        firstHex[3] = new Point(hexIncircle, halfHexSize * 4);
        firstHex[4] = new Point(0, halfHexSize * 3);
        firstHex[5] = new Point(0, halfHexSize);

        int preferredWidth = fieldSize.width * 2 * this.hexIncircle + 1;
        int preferredHeight = (fieldSize.height * 3  + 1) * halfHexSize + 1;
        this.preferredSize = new Dimension(preferredWidth, preferredHeight);
    }

    private class Span {
        private Point startPoint;
        private int width;

        Span(Point start, int width) {
            startPoint = start;
            this.width = width;
        }

        Point getStartPoint() {
            return startPoint;
        }

        int getWidth() {
            return width;
        }
    }

    private void spanFill(BufferedImage image, Point seed, Color color) {
        Color oldColor = new Color(image.getRGB(seed.x, seed.y));
        if (color == null || color.equals(oldColor)) {
            return;
        }

        Stack<Span> spans = new Stack<>();
        spans.push(getSpan(image, seed, color));
        while (!spans.empty()) {
            Span span = spans.pop();
            Point startPoint = span.getStartPoint();

            for(int i = 0, width = span.getWidth(); i < width; ++i) {
                image.setRGB(startPoint.x + i, startPoint.y, color.getRGB());
            }

            findNearSpans(spans, image, span, oldColor);
        }
    }

    private void findNearSpans(Stack<Span> spans, BufferedImage image, Span span, Color oldColor) {
        Point startPoint = span.getStartPoint();
        for (int offset : new int[]{-1, 1}) {
            int y = startPoint.y + offset;
            if (y < 0 || y >= image.getHeight()) {
                continue;
            }

            for (int i = 0, width = span.getWidth(); i < width;) {
                int x = i + startPoint.x;
                if (image.getRGB(x, y) != oldColor.getRGB()) {
                    ++i;
                    continue;
                }
                Span newSpan = getSpan(image, new Point(x, y), oldColor);
                spans.push(newSpan);
                i += newSpan.width;
            }
        }
    }

    private Span getSpan(BufferedImage image, Point crds, Color color) {
        int rgbColor = color.getRGB();

        int x0 = crds.x;
        int y = crds.y;

        while(x0 > 0 && rgbColor == image.getRGB(x0 - 1, y)) {
            --x0;
        }

        int x1 = crds.x;
        int width = image.getWidth() - 1;
        while (x1 < width && rgbColor == image.getRGB(x1 + 1, y)) {
            ++x1;
        }

        return new Span(new Point(x0, y), x1 - x0 + 1);
    }

    private void drawField(BufferedImage background, int x0, int y0, int x1, int y1) {
        IField field = fieldModel.getActiveField();
        Dimension fieldSize = field.getSize();

        boolean isImpactVisible = propertiesModel.isImpactVisible();
        int impactFontSize = propertiesModel.getImpactFontSize();
        Graphics2D backgroundGraphics = background.createGraphics();
        backgroundGraphics.setPaint(Color.BLACK);
        backgroundGraphics.setFont(new Font("Dialog", Font.PLAIN, impactFontSize));

        for(int y = y0; y < y1; ++y) {

            int currentLineCount = Math.min(fieldSize.width + (y % 2 == 0 ? 0 : -1), x1);

            for(int x = x0; x < currentLineCount; ++x) {

                int shownX = x - x0;
                int shownY = y - y0;

//                System.out.println("ShownX: " + shownX + ", shownY: " + shownY);

                int offsetX = (y % 2) == 0 ? 0 : hexIncircle;
                Point[] points = shiftPoints(shownX, shownY, offsetX);
                drawPolygon(background, points);

//                drawHexagon(background, shownX, shownY, offsetX);

                Point center = new Point(points[5].x + hexIncircle, points[0].y + halfHexSize * 2);
                if(field.get(x, y) == CellState.ALIVE) {
//                    System.out.println("Alive: " + new Point(x, y));
                    spanFill(background, center, aliveColor);
                }

                if (isImpactVisible && hexSize >= 18) {
                    printImpact(backgroundGraphics, x, y, center);
                }
            }
        }
    }

    private void printImpact(Graphics2D backgroundGraphics, int x, int y, Point pos) {
        FontMetrics fontMetrics = backgroundGraphics.getFontMetrics();
        int impactFontSize = propertiesModel.getImpactFontSize();
        double impact = fieldModel.getImpact(x, y);
        int intImpact = (int)impact;
        String impactStr = (Double.compare(impact, intImpact) == 0) ? String.format("%d", intImpact) : String.format("%.1f", impact);
        backgroundGraphics.drawString(impactStr, pos.x - fontMetrics.stringWidth(impactStr) / 2, pos.y + impactFontSize / 2);
    }

    private Point[] shiftPoints(int x, int y, int extraOffsetX) {
        int offsetX = 2 * hexIncircle;
        int offsetY = halfHexSize * 3;
        Point[] points = new Point[firstHex.length];
        for(int i = 0; i < firstHex.length; ++i) {
            points[i] = new Point(firstHex[i].x + x * offsetX + extraOffsetX, firstHex[i].y + y * offsetY);
        }

        return points;
    }

    private void drawHexagon(BufferedImage image, int x, int y, int extraOffsetX) {
        int offsetX = 2 * hexIncircle;
        int offsetY = halfHexSize * 3;
        Point[] points = new Point[firstHex.length];
        for(int i = 0; i < firstHex.length; ++i) {
            points[i] = new Point(firstHex[i].x + x * offsetX + extraOffsetX, firstHex[i].y + y * offsetY);
        }

        drawPolygon(image, points);
    }

    private interface ILinePainter {
        void draw(int x0, int y0, int x1, int y1);
    }

    private void drawPolygon(BufferedImage image, Point[] points) {
        ILinePainter linePainter;

        if(propertiesModel.getLineThickness() > 1) {
            linePainter = new ILinePainter() {
                private Graphics2D graphics2D;
                {
                    graphics2D = image.createGraphics();
                    graphics2D.setStroke(new BasicStroke(propertiesModel.getLineThickness()));
                    graphics2D.setPaint(Color.BLACK);
                }

                @Override
                public void draw(int x0, int y0, int x1, int y1) {
                    graphics2D.drawLine(x0, y0, x1, y1);
                }
            };
        } else {
            linePainter = (x0, y0, x1, y1) -> {
                drawLine(image, x0, y0, x1, y1);
            };
        }


        for(int i = 0; i < points.length; ++i) {
            int next = (i + 1) % points.length;

            try {
                linePainter.draw(points[i].x, points[i].y, points[next].x, points[next].y);
            } catch (Exception e) {
                System.out.println(points[i] + ", " + points[next]);
                System.out.println("Size: " + image.getWidth() + ", " + image.getHeight());
            }
        }
    }

    private void drawLine(BufferedImage field, int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int dirX = dx == 0 ? 0 : (x1 - x0) / dx;
        int dirY = dy == 0 ? 0 : (y1 - y0) / dy;

        int maxLength;
        int minLength;
        int pdx;
        int pdy;
        if (dx > dy) {
            pdx = dirX;
            pdy = 0;
            minLength = dy;
            maxLength = dx;
        } else {
            pdx = 0;
            pdy = dirY;
            minLength = dx;
            maxLength = dy;
        }

        int x = x0;
        int y = y0;
        int err = maxLength / 2;
        field.setRGB(x, y, Color.BLACK.getRGB());

        for (int i = 0; i < maxLength; i++) {
            err -= minLength;
            if (err < 0) {
                err += maxLength;
                x += dirX;
                y += dirY;
            } else {
                x += pdx;
                y += pdy;
            }

            field.setRGB(x, y, Color.BLACK.getRGB());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    @Override
    public Dimension getMinimumSize() {
        return preferredSize;
    }
}
