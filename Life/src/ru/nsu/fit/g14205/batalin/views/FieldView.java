package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;
import ru.nsu.fit.g14205.batalin.models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

/**
 * Created by kir55rus on 14.02.17.
 */
public class FieldView extends JLabel implements Observer {
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

        propertiesModel.addObserver(PropertiesModelEvent.SIZE_CHANGED, () -> {
            updSize(propertiesModel);
            repaint();
        });

        fieldModel.addObserver(FieldModelEvent.FIELD_UPDATED, this::repaint);
        propertiesModel.addObserver(PropertiesModelEvent.IMPACT_VISIBLE_CHANGED, this::repaint);
    }

    @Override
    public void update(Observable observable, Object o) {
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

    private void spanFill(BufferedImage image, Point seed, Color color) {
        Color oldColor = new Color(image.getRGB(seed.x, seed.y));
        if (color == null || color.equals(oldColor)) {
            return;
        }

        Stack<Rectangle> spans = new Stack<>();
        spans.push(getSpan(image, seed));
        while (!spans.empty()) {
            Rectangle span = spans.pop();

            for(int i = 0; i < span.width; ++i) {
                image.setRGB(span.x + i, span.y, color.getRGB());
            }

            findNearSpans(spans, image, span, oldColor);
        }
    }

    private void findNearSpans(Stack<Rectangle> spans, BufferedImage image, Rectangle span, Color oldColor) {
        for (int offset : new int[]{-1, 1}) {
            int y = span.y + offset;
            if (y < 0 || y >= image.getHeight()) {
                continue;
            }

            for (int i = 0; i < span.width;) {
                int x = i + span.x;
                if (image.getRGB(x, y) != oldColor.getRGB()) {
                    ++i;
                    continue;
                }
                Rectangle newSpan = getSpan(image, new Point(x, y));
                spans.push(newSpan);
                i += newSpan.width;
            }
        }
    }

    private Rectangle getSpan(BufferedImage image, Point crds) {
        int color = image.getRGB(crds.x, crds.y);

        int x0 = crds.x;
        int y = crds.y;

        while(x0 > 0 && color == image.getRGB(x0 - 1, y)) {
            --x0;
        }

        int x1 = crds.x;
        int width = image.getWidth() - 1;
        while (x1 < width && color == image.getRGB(x1 + 1, y)) {
            ++x1;
        }

        return new Rectangle(x0, y, x1 - x0 + 1, 1);
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

                if (isImpactVisible) {
                    double impact = fieldModel.getImpact(x, y);
                    backgroundGraphics.drawString(String.format("%.1f", impact), center.x, center.y + impactFontSize / 2);
                }
            }
        }
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

    private void drawPolygon(BufferedImage image, Point[] points) {
        for(int i = 0; i < points.length; ++i) {
            int next = (i + 1) % points.length;

            try {
                drawLine(image, points[i].x, points[i].y, points[next].x, points[next].y);
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
